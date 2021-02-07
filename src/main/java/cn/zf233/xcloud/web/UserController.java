package cn.zf233.xcloud.web;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.RequestBody;
import cn.zf233.xcloud.commom.ResponseCodeENUM;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.exception.LoginException;
import cn.zf233.xcloud.exception.RegistException;
import cn.zf233.xcloud.exception.UserDetailUpdateException;
import cn.zf233.xcloud.exception.UserLoginException;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.VersionPermissionService;
import cn.zf233.xcloud.util.JsonUtil;
import cn.zf233.xcloud.vo.FileVo;
import cn.zf233.xcloud.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zf233 on 2020/12/25
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @Resource
    private VersionPermissionService versionPermissionService;

    // phone start
    @RequestMapping(value = "/home", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<List<FileVo>> home(String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }

        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }

        if (checkUserDetailOfApp(body.getUser())) {
            return fileService.home(body.getUser(), body.getParentid(), body.getSortFlag(), body.getSortType(), body.getMatchCode(), null);
        }

        return ServerResponse.createByErrorIllegalArgument("登陆失效");
    }

    // 登陆
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<UserVo> userLogin(String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }

        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }

        return userService.login(body.getUser());
    }

    // 注册
    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<UserVo> userRegister(String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }

        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }

        return userService.regist(body.getUser());
    }

    // 更新用户信息
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<UserVo> userUpdate(String requestBody) throws UserDetailUpdateException {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }

        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }

        if (checkUserDetailOfApp(body.getUser())) {
            return userService.update(body.getUser());
        }

        return ServerResponse.createByErrorIllegalArgument("登陆失效");
    }

    // 注册检查 异步AJAX 响应Json (预留)
    @RequestMapping(value = "/check/info", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse userInfoCheck(String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }

        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }

        return userService.checkUserInfoExists(body.getUser());
    }
    // phone end


    // browse start
    // 用户主页
    @RequestMapping("/browse/home")
    public String requestIndex(HttpSession session,
                               @RequestParam(required = false) Integer parentid,
                               @RequestParam(required = false) Integer sortFlag,
                               @RequestParam(required = false) Integer sortType,
                               @RequestParam(required = false) String matchCode,
                               @RequestParam(required = false) Integer classify) {
        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        if (userVo != null) {

            User user = new User();
            user.setId(userVo.getId());

            if (parentid == null) {
                parentid = (Integer) session.getAttribute(Const.PARENTID);
            }

            ServerResponse<List<FileVo>> homeResponse = fileService.home(user, parentid, sortFlag, sortType, matchCode, classify);
            if (!homeResponse.isSuccess()) {
                parentid = -1;
                homeResponse = fileService.home(user, parentid, sortFlag, sortType, matchCode, classify);
            }

            UserVo userByPrimarykey = userService.getUserVoByPrimarykey(user.getId());
            session.removeAttribute(Const.ABSOLUTEPATH);
            session.removeAttribute(Const.PARENTID);
            session.removeAttribute(Const.CURRENT_USER);

            if (StringUtils.isBlank(matchCode)) {
                session.setAttribute(Const.ABSOLUTEPATH, homeResponse.getAbsolutePath());
                session.setAttribute(Const.PARENTID, parentid);
            }
            session.setAttribute(Const.CURRENT_USER, userByPrimarykey);
            session.setAttribute(Const.SessionAttributeCode.FILE_VOS, homeResponse.getData());
        }

        return "home";
    }

    // 登陆
    @RequestMapping(value = "/browse/login", method = RequestMethod.POST)
    public String userLogin(User user, HttpSession session) throws LoginException {
        ServerResponse<UserVo> loginResponse = userService.login(user);
        if (loginResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, loginResponse.getData());
            return "redirect:/user/browse/home";
        }

        if (loginResponse.getStatus() == Const.CheckEmailENUM.NOT_CHECK.getCode()) {

            Map<String, String> map = checkJump(loginResponse.getStatus(), loginResponse.getData().getId());
            if (map != null) {
                session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, loginResponse.getMsg());
                session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, map.get("uri"));
                session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, map.get("title"));

                return "redirect:/user/browse/jump?jump=notice";
            }
        }

        throw new UserLoginException(loginResponse.getMsg());
    }

    // 登出
    @RequestMapping("/browse/logout")
    public String userLogout(HttpSession session) {
        session.invalidate();

        return "redirect:/user/browse/jump?jump=index";
    }

    // 注册
    @RequestMapping(value = "/browse/regist", method = RequestMethod.POST)
    public String userRegister(HttpSession session, User user) throws RegistException {
        ServerResponse<UserVo> registResponse = userService.regist(user);
        if (registResponse.isSuccess()) {
            session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, registResponse.getMsg());
            session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, "user/browse/again?id=" + registResponse.getData().getId());
            session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, "重新获取邮箱验证");

            return "redirect:/user/browse/jump?jump=notice";
        }

        throw new RegistException(registResponse.getMsg());
    }

    // 更新用户信息
    @RequestMapping(value = "/browse/update", method = RequestMethod.POST)
    public String userUpdate(HttpSession session, User user) throws UserDetailUpdateException {
        ServerResponse<UserVo> updateResponse = userService.update(user);
        if (updateResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, updateResponse.getData());

            return "redirect:/user/browse/home";
        }

        throw new UserDetailUpdateException(updateResponse.getMsg());
    }

    // 验证验证邮件
    @RequestMapping(value = "/browse/check")
    public String userCheck(HttpSession session, User user, String uuid) {
        ServerResponse serverResponse = userService.checkRegistUser(user, uuid);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, serverResponse.getMsg());
            session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, "user/browse/jump?jump=login");
            session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, "点此返回");

            return "redirect:/user/browse/jump?jump=notice";
        }

        return getJump(session, user, serverResponse);
    }

    // 注册检查 异步AJAX 响应Json
    @RequestMapping(value = "/browse/check/info", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse userInfoCheck(User user) {
        return userService.checkUserInfoExists(user);
    }

    // 再次发送验证邮件
    @RequestMapping("/browse/again")
    public String again(HttpSession session, User user) {
        ServerResponse serverResponse = userService.againSendUserRegistEmail(user);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, serverResponse.getMsg());
            session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, "user/browse/again?id=" + user.getId());
            session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, "重新获取邮箱验证");

            return "redirect:/user/browse/jump?jump=notice";
        }

        return getJump(session, user, serverResponse);
    }

    // 用于页面间的跳转
    @RequestMapping("/browse/jump")
    public String jump(@RequestParam(required = false) String jump,
                       HttpSession session) {

        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);

        // 查看jump目标是否存在
        if (Const.PageNameENUM.exists(jump)) { //存在

            if (userVo == null) { // 未登陆

                if (Const.PageNameENUM.PAGE_LOGIN.getName().equals(jump) ||
                        Const.PageNameENUM.PAGE_REGIST.getName().equals(jump) ||
                        Const.PageNameENUM.PAGE_NOTICE.getName().equals(jump)) { // 可以链接转到到登陆、注册、通知页面
                    return jump;
                }

                return "index"; // 否则转到欢迎页
            }

            if (Const.PageNameENUM.PAGE_USER_DETAIL.getName().equals(jump)) { // 已登陆只可以转到用户信息页
                return jump;
            }

            return "home"; // 否则转到用户主页
        }

        if (userVo == null) { // 未登录 转到欢迎页
            return "index";
        }

        return "home"; // 已登陆 转到用户主页
    }

    private String getJump(HttpSession session, User user, ServerResponse serverResponse) {
        Map<String, String> map = checkJump(serverResponse.getStatus(), user.getId());
        if (map != null) {
            session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, serverResponse.getMsg());
            session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, map.get("uri"));
            session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, map.get("title"));

            return "redirect:/user/browse/jump?jump=notice";
        }

        throw new RegistException(serverResponse.getMsg());
    }

    // 统一解决邮箱注册跳转逻辑繁复的问题
    private Map<String, String> checkJump(Integer code, Integer userId) {

        // 根据code获取对应枚举
        Const.CheckEmailENUM exists = Const.CheckEmailENUM.exists(code);
        Map<String, String> map = new HashMap<>();
        if (exists != null) { // 存在
            switch (exists.getCode()) {
                case 0:
                case 1:
                case 2:
                case 6:
                    map.put("uri", "user/browse/again?id=" + userId);
                    map.put("title", "重新获取邮箱验证");
                    break;
                case 3:
                case 4:
                case 5:
                    map.put("uri", "user/browse/jump?jump=login");
                    map.put("title", "点此返回");
                    break;
            }

            return map;
        }

        // 不存在
        return null;
    }
    // browse end

    // App端登陆检查用户细节和App版本
    private Boolean checkUserDetailOfApp(User user) {
        ServerResponse<UserVo> loginResponse = userService.login(user);

        return loginResponse.isSuccess();
    }
}