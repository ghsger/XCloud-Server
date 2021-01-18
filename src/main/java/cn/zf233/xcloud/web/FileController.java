package cn.zf233.xcloud.web;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ResponseCodeENUM;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.VersionPermissionService;
import cn.zf233.xcloud.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
        return fileService.saveFile(user, myFile, remark, parentid);
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

        ServerResponse serverResponse = fileService.saveFile(user, myFile, remark, parentid);

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
}


