package cn.zf233.xcloud.web;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ResponseCodeENUM;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.VersionPermissionService;
import cn.zf233.xcloud.util.FastDFSUtil;
import cn.zf233.xcloud.util.RedisUtil;
import cn.zf233.xcloud.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.csource.common.MyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Created by zf233 on 2020/11/27
 */
@Controller
@SessionAttributes
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private VersionPermissionService versionPermissionService;

    // phone start
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse fileUpload(HttpSession session,
                                     MultipartFile myFile,
                                     User user,
                                     @RequestParam(required = false) String remark,
                                     Integer parentid,
                                     String appVersionCode) throws IOException {
        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return checkDetailOfAppResponse;
        }

        File uploadFile = getFile(myFile, remark, user);
        userService.updateUserGrowthValueByPrimaryKeyTask(user.getId());
        ServerResponse serverResponse = fileService.saveFile(user, uploadFile, parentid);
        if (!serverResponse.isSuccess()) {
            redisUtil.removeFile(uploadFile.getRedisCacheName());
        }
        return serverResponse;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse fileDelete(HttpSession session,
                                     Integer fileid,
                                     User user,
                                     String appVersionCode) {
        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return checkDetailOfAppResponse;
        }

        userService.updateUserGrowthValueByPrimaryKeyTask(user.getId());
        return fileService.removeFileOrFolder(fileid, user);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> fileDownload(HttpSession session,
                                               Integer fileid,
                                               User user,
                                               String appVersionCode) throws IOException, MyException {
        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return null;
        }

        return getResponseEntity(fileid, user.getId());
    }

    @RequestMapping(value = "/createfolder", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse createFolder(HttpSession session,
                                       User user,
                                       String foldername,
                                       Integer parentid,
                                       String appVersionCode) {
        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return checkDetailOfAppResponse;
        }

        userService.updateUserGrowthValueByPrimaryKeyTask(user.getId());
        return fileService.createFolder(user, foldername, parentid);
    }
    // phone end

    // browse start
    @RequestMapping(value = "/browse/upload", method = RequestMethod.POST)
    public String fileUpload(MultipartFile myFile, HttpSession session,
                             @RequestParam(required = false) String remark,
                             Integer parentid) throws IOException {
        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User currentUser = new User();
        currentUser.setId(userVo.getId());

        File uploadFile = getFile(myFile, remark, currentUser);
        ServerResponse serverResponse = fileService.saveFile(currentUser, uploadFile, parentid);
        if (!serverResponse.isSuccess()) {
            session.setAttribute(Const.PARENTID, -1);
            fileService.saveFile(currentUser, uploadFile, -1);
        }
        userService.updateUserGrowthValueByPrimaryKeyTask(currentUser.getId());
        return "redirect:/user/browse/index";
    }

    @RequestMapping(value = "/browse/delete")
    public String fileDelete(@RequestParam(required = false) Integer fileid, HttpSession session) {
        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User currentUser = new User();
        currentUser.setId(userVo.getId());
        ServerResponse serverResponse = fileService.removeFileOrFolder(fileid, currentUser);
        if (!serverResponse.isSuccess()) {
            session.setAttribute(Const.PARENTID, -1);
        }
        userService.updateUserGrowthValueByPrimaryKeyTask(currentUser.getId());
        return "redirect:/user/browse/index";
    }

    @RequestMapping(value = "/browse/download")
    public ResponseEntity<byte[]> fileDownload(Integer fileid, HttpSession session) throws IOException, MyException {
        UserVo currentUser = (UserVo) session.getAttribute(Const.CURRENT_USER);
        return getResponseEntity(fileid, currentUser.getId());
    }

    @RequestMapping(value = "/browse/createfolder")
    public String createFolderBrowse(String foldername, Integer parentid, HttpSession session) {
        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User currentUser = new User();
        currentUser.setId(userVo.getId());
        currentUser.setUsername(userVo.getUsername());
        ServerResponse serverResponse = fileService.createFolder(currentUser, foldername, parentid);
        if (!serverResponse.isSuccess()) {
            session.setAttribute(Const.PARENTID, -1);
            fileService.createFolder(currentUser, foldername, -1);
        }
        userService.updateUserGrowthValueByPrimaryKeyTask(currentUser.getId());
        return "redirect:/user/browse/index";
    }
    // browse end


    private ResponseEntity<byte[]> getResponseEntity(Integer fileid, Integer id) throws IOException, MyException {
        File targetFile = fileService.getFileByFileId(fileid, id);
//        file.setOldFileName(URLEncoder.encode(file.getOldFileName(), "UTF-8"));
        targetFile.setOldFileName(new String(targetFile.getOldFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        byte[] download;
        if (StringUtils.isNotBlank(targetFile.getRedisCacheName())) {
            download = redisUtil.getFile(targetFile.getRedisCacheName());
        } else if (StringUtils.isNotBlank(targetFile.getGroupName()) && StringUtils.isNotBlank(targetFile.getRemoteFilePath())) {
            download = FastDFSUtil.download(targetFile.getGroupName(), targetFile.getRemoteFilePath());
        } else {
            download = new byte[0];
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(targetFile.getFileSize());
        headers.setContentDispositionFormData("attachment", targetFile.getOldFileName());
        userService.updateUserGrowthValueByPrimaryKeyTask(id);
        return new ResponseEntity<>(download, headers, HttpStatus.OK);
    }

    private File assembleFile(User user, String remark, String fileName, long fileSize, String fileType, String redisCacheName) {
        File file = new File();
        file.setUserId(user.getId());
        file.setFolder(0);
        file.setOldFileName(fileName);
        file.setFileSize(fileSize);
        file.setFileType(fileType);
        file.setRemark(remark);
        file.setDownloadCount(0);
        file.setRedisCacheName(redisCacheName);
        file.setUploadTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        file.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        return file;
    }

    private File getFile(MultipartFile myFile, String remark, User currentUser) throws IOException {
        byte[] fileBytes = myFile.getBytes();
        String fileName = Objects.requireNonNull(myFile.getOriginalFilename()).replace(" ", "");
        long fileSize = myFile.getSize();
        String fileType = myFile.getContentType();
        String redisCacheName = redisUtil.saveFile(fileBytes);
        return assembleFile(currentUser, remark, fileName, fileSize, fileType, redisCacheName);
    }

    private ServerResponse checkDetailOfApp(String appVersionCode, User user) {
        if (versionPermissionService.testVersionCodeOfFileRequest(appVersionCode)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }
        if (userService.login(user).isSuccess()) {
            return null;
        }
        return ServerResponse.createByErrorIllegalArgument("登陆失效");
    }
}


