package cn.zf233.xcloud.web;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ResponseCodeENUM;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.VersionPermissionService;
import cn.zf233.xcloud.util.OSSUtil;
import cn.zf233.xcloud.util.RedisUtil;
import cn.zf233.xcloud.vo.UserVo;
import com.aliyun.oss.OSS;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zf233 on 2020/11/27
 */
@Controller
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Resource
    private VersionPermissionService versionPermissionService;

    @Resource
    private OSSUtil ossUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    // phone start
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse fileUpload(MultipartFile[] myFile,
                                     User user,
                                     @RequestParam(required = false) String remark,
                                     Integer parentid,
                                     String appVersionCode) throws IOException {

        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return checkDetailOfAppResponse;
        }

        if (myFile.length == 0) {
            throw new IOException("上传文件为空");
        }

        UserVo userVoByPrimarykey = userService.getUserVoByPrimarykey(user.getId());
        if (userVoByPrimarykey.getUseCapacity() + myFile.length > userVoByPrimarykey.getLevel() * 10) {
            throw new IOException("上传失败(空间已满)");
        }

        List<File> uploadFileInfos = getUploadFileInfosAndUploadFileToOSS(myFile, remark, user);
        if (uploadFileInfos == null) {
            throw new IOException("移除命名不规范的文件后,上传列表为空。未成功上传任何文件!");
        }

        return fileService.saveFile(user, uploadFileInfos, remark, parentid);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse fileDelete(Integer[] fileid,
                                     User user,
                                     String appVersionCode) {

        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return checkDetailOfAppResponse;
        }

        return fileService.removeFileOrFolder(fileid, user);
    }

    @RequestMapping(value = "/createfolder", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse createFolder(User user,
                                       String foldername,
                                       Integer parentid,
                                       String appVersionCode) {
        ServerResponse checkDetailOfAppResponse = checkDetailOfApp(appVersionCode, user);
        if (checkDetailOfAppResponse != null) {
            return checkDetailOfAppResponse;
        }

        return fileService.createFolder(user, foldername, parentid);
    }
    // phone end

    // browse start
    @RequestMapping(value = "/browse/upload", method = RequestMethod.POST)
    public String fileUpload(MultipartFile[] myFile, HttpSession session,
                             @RequestParam(required = false) String remark,
                             Integer parentid) throws IOException {

        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User user = new User();
        user.setId(userVo.getId());

        UserVo userVoByPrimarykey = userService.getUserVoByPrimarykey(user.getId());
        if (userVoByPrimarykey.getUseCapacity() + myFile.length > userVoByPrimarykey.getLevel() * 10) {
            throw new IOException("上传失败(空间已满)");
        }

        List<File> uploadFileInfos = getUploadFileInfosAndUploadFileToOSS(myFile, remark, user);
        if (uploadFileInfos == null) {
            throw new IOException("移除命名不规范的文件后,上传列表为空。未成功上传任何文件!");
        }

        ServerResponse serverResponse = fileService.saveFile(user, uploadFileInfos, remark, parentid);
        if (!serverResponse.isSuccess()) {
            throw new IOException(serverResponse.getMsg());
        }

        return "redirect:/user/browse/home";
    }

    @RequestMapping(value = "/browse/delete", method = RequestMethod.POST)
    public String fileDelete(Integer[] fileid, HttpSession session) {

        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User currentUser = new User();
        currentUser.setId(userVo.getId());

        ServerResponse serverResponse = fileService.removeFileOrFolder(fileid, currentUser);
        if (!serverResponse.isSuccess()) {
            session.setAttribute(Const.PARENTID, -1);
        }

        return "redirect:/user/browse/home";
    }

    @RequestMapping(value = "/browse/createfolder", method = RequestMethod.POST)
    public String createFolderBrowse(String foldername, Integer parentid, HttpSession session) {

        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User user = new User();
        user.setId(userVo.getId());
        user.setUsername(userVo.getUsername());

        ServerResponse serverResponse = fileService.createFolder(user, foldername, parentid);
        if (!serverResponse.isSuccess()) {
            session.setAttribute(Const.PARENTID, -1);
            if (StringUtils.isNotBlank(foldername)) {
                fileService.createFolder(user, foldername, -1);
            }
        }

        return "redirect:/user/browse/home";
    }

    @RequestMapping(value = "/browse/share", method = RequestMethod.POST)
    public @ResponseBody
    String fileShare(Integer fileid, HttpSession session) {

        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        User user = new User();
        user.setId(userVo.getId());
        user.setUsername(userVo.getUsername());

        ServerResponse serverResponse = fileService.getFileShareQrURL(fileid, user);
        if (serverResponse.isSuccess()) {
            return serverResponse.getData().toString();
        }
        return null;
    }

    // browse end

    private ServerResponse checkDetailOfApp(String appVersionCode, User user) {
        if (versionPermissionService.testVersionCodeOfFileRequest(appVersionCode)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }

        if (userService.login(user).isSuccess()) {
            return null;
        }

        return ServerResponse.createByErrorIllegalArgument("登陆失效");
    }


    // 组装上传文件对象
    private File assembleFile(User user, String remark, String fileName, long fileSize, String fileType) {
        File file = new File();
        file.setUserId(user.getId());
        file.setFolder(0);
        if (fileName.contains(".")) {
            file.setRandomFileName(fileName.substring(0, fileName.lastIndexOf(".")) + "_" + Const.DOMAIN_NAME + UUID.randomUUID().toString().replace("_", "") + fileName.substring(fileName.lastIndexOf(".")));
        } else {
            file.setRandomFileName(fileName + "_" + Const.DOMAIN_NAME + UUID.randomUUID().toString().replace("_", ""));
        }
        file.setOldFileName(fileName);
        file.setFileSize(fileSize);
        file.setFileType(fileType);
        file.setRemark(remark);
        file.setUploadTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        file.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());

        return file;
    }

    // 协调文件上传，文件先缓存到Redis，而后异步持久化至OSS
    private List<File> getUploadFileInfosAndUploadFileToOSS(MultipartFile[] myFile, String remark, User user) {

        // 文件名合法的文件列表
        List<File> files = new ArrayList<>();
        // 顺序缓存至Redis的key 对应files顺序
        Queue<String> queue = new LinkedList<>();

        for (MultipartFile multipartFile : myFile) {

            String fileName = multipartFile.getOriginalFilename();

            if (StringUtils.isBlank(fileName)) {
                continue;
            }

            fileName = fileName.replace(" ", "");

            if (checkUploadFileName(fileName)) {

                long fileSize = multipartFile.getSize();
                String fileType = multipartFile.getContentType();
                File file = assembleFile(user, remark, fileName, fileSize, fileType);
                try {
                    String fileCacheKey = Const.DOMAIN_NAME + "_" + UUID.randomUUID().toString();
                    redisUtil.setFileCache(fileCacheKey, multipartFile.getBytes());

                    queue.offer(fileCacheKey);
                    files.add(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 文件名合法的文件列表不为空
        if (files.size() > 0) {

            // 异步持久操作
            taskExecutor.execute(() -> {
                OSS ossClient = ossUtil.getOSSClient();

                for (File file : files) {

                    String fileCacheKey = queue.poll();
                    if (StringUtils.isNotBlank(fileCacheKey)) {

                        if (redisUtil.cacheKeyExists(fileCacheKey)) { // key存在
                            byte[] fileCache = redisUtil.getFileCache(fileCacheKey);
                            redisUtil.removeFileCache(fileCacheKey);
                            ossUtil.upload(ossClient, file.getRandomFileName(), file.getOldFileName(), fileCache);
                        }
                    }
                }

                ossClient.shutdown();
            });

            return files;
        }

        return null;
    }

    // 检车上传文件名是否合法
    public Boolean checkUploadFileName(String fileName) {
        final String format = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w-_.,()（）《》]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(fileName);
        return !matcher.find() && fileName.contains(".");
    }
}