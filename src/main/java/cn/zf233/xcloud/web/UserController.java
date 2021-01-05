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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by zf233 on 2020/12/25
 */
@Controller
@SessionAttributes
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
    ServerResponse<List<FileVo>> home(HttpSession session, String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }
        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }
        if (checkUserDetailOfApp(body.getUser())) {
            return fileService.home(body.getUser(), body.getParentid(), body.getSortFlag(), body.getSortType(), body.getMatchCode());
        }
        return ServerResponse.createByErrorIllegalArgument("登陆失效");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<UserVo> userLogin(HttpSession session, String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }
        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }
        return userService.login(body.getUser());
    }

    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<UserVo> userRegister(HttpSession session, String requestBody) {
        RequestBody body = JsonUtil.toObject(requestBody, RequestBody.class);
        if (body == null) {
            return ServerResponse.createByErrorIllegalArgument();
        }
        if (versionPermissionService.testVersionCodeOfUserRequest(body.getVersionPermission())) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.VERSION_FAILURE.getCode(), "过时的App版本");
        }
        return userService.regist(body.getUser(), body.getInviteCode());
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    ServerResponse<UserVo> userUpdate(HttpSession session, String requestBody) throws UserDetailUpdateException {
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
    // phone end

    // browse start
    @RequestMapping(value = "/browse/login", method = RequestMethod.POST)
    public String userLogin(User user, HttpSession session) throws LoginException {
        ServerResponse<UserVo> loginResponse = userService.login(user);
        if (loginResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, loginResponse.getData());
            return "redirect:/user/browse/index";
        } else {
            throw new UserLoginException(loginResponse.getMsg());
        }
    }

    @RequestMapping("/browse/index")
    public ModelAndView requestIndex(HttpSession session,
                                     @RequestParam(required = false) Integer parentid,
                                     @RequestParam(required = false) Integer sortFlag,
                                     @RequestParam(required = false) Integer sortType,
                                     @RequestParam(required = false) String matchCode) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/pages/index/home.jsp");
        UserVo userVo = (UserVo) session.getAttribute(Const.CURRENT_USER);
        if (userVo != null) {
            User user = new User();
            user.setId(userVo.getId());
            if (parentid == null) {
                parentid = (Integer) session.getAttribute(Const.PARENTID);
            }
            ServerResponse<List<FileVo>> homeResponse = fileService.home(user, parentid, sortFlag, sortType, matchCode);
            if (!homeResponse.isSuccess()) {
                parentid = -1;
                homeResponse = fileService.home(user, parentid, sortFlag, sortType, matchCode);
            }
            UserVo userByPrimarykey = userService.getUserByPrimarykey(user.getId());
            session.removeAttribute(Const.SessionAttributeCode.FILE_NULL_TYPE);
            session.removeAttribute(Const.ABSOLUTEPATH);
            session.removeAttribute(Const.PARENTID);
            session.removeAttribute(Const.CURRENT_USER);
            if (StringUtils.isNotBlank(matchCode) && homeResponse.getData().size() == 0) {
                session.setAttribute(Const.SessionAttributeCode.FILE_NULL_TYPE, Const.SessionAttributeCode.FILE_NULL_TYPE);
            }
            if (StringUtils.isBlank(matchCode)) {
                session.setAttribute(Const.ABSOLUTEPATH, homeResponse.getAbsolutePath());
                session.setAttribute(Const.PARENTID, parentid);
            }
            session.removeAttribute(Const.CURRENT_USER);
            session.setAttribute(Const.CURRENT_USER, userByPrimarykey);
            session.setAttribute(Const.SessionAttributeCode.FILE_VOS, homeResponse.getData());
        }
        return modelAndView;
    }

    @RequestMapping("/browse/logout")
    public String userLogout(HttpSession session) {
        session.removeAttribute("fileNullType");
        session.removeAttribute(Const.CURRENT_USER);
        session.removeAttribute(Const.PARENTID);
        session.removeAttribute(Const.ABSOLUTEPATH);
        session.removeAttribute(Const.SessionAttributeCode.ERROR_MSG);
        session.removeAttribute(Const.SessionAttributeCode.ERROR_BACK);
        return "redirect:/user/browse/index";
    }

    @RequestMapping("/browse/regist")
    public String userRegister(HttpSession session, User user, String inviteCode) throws RegistException {
        ServerResponse<UserVo> registResponse = userService.regist(user, inviteCode);
        if (registResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, registResponse.getData());
            return "redirect:/user/browse/index";
        }
        throw new RegistException(registResponse.getMsg());
    }

    @RequestMapping("/browse/update")
    public String userUpdate(HttpSession session, User user) throws UserDetailUpdateException {
        ServerResponse<UserVo> updateResponse = userService.update(user);
        if (updateResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, updateResponse.getData());
            return "redirect:/user/browse/index";
        }
        throw new UserDetailUpdateException(updateResponse.getMsg());
    }
    // browse end

    private Boolean checkUserDetailOfApp(User user) {
        ServerResponse<UserVo> loginResponse = userService.login(user);
        return loginResponse.isSuccess();
    }
}