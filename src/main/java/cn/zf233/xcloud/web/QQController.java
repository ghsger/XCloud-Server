package cn.zf233.xcloud.web;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.vo.UserVo;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by zf233 on 2020/01/15
 */
@Controller
@RequestMapping("/account")
public class QQController {

    @Resource
    private UserService userService;

    @RequestMapping("/qq/login")
    public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            response.setContentType("text/html;charset=utf-8");
            String authorizeURL = new Oauth().getAuthorizeURL(request);
            return "redirect:" + authorizeURL;
        } catch (QQConnectException e) {
            session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, "点此返回");
            session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, "user/browse/jump?jump=login");
            return "redirect:/user/browse/jump?jump=notice";
        }
    }

    // App QQ登陆接口
    /*@RequestMapping("qq/login/phone")
    public ServerResponse loginOfPhone(String openId, String nickname) {
        if (StringUtils.isBlank(openId) || StringUtils.isBlank(nickname)) {
            return ServerResponse.createByErrorMessage("登陆失败");
        }
        User user = new User();
        user.setOpenId(openId);
        user.setNickname(removeNonBmpUnicode(nickname));
        return userService.qqLogin(user);
    }*/

    @RequestMapping("/qq/login/callback")
    public String qqCallback(HttpServletRequest request, HttpSession session) throws QQConnectException {

        AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
        String accessToken;
        String openId;
        long tokenExpireIn;

        if ("".equals(accessTokenObj.getAccessToken())) {
            session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, "没有获取到QQ响应参数");
        } else {

            // 获取accessToken
            accessToken = accessTokenObj.getAccessToken();
            tokenExpireIn = accessTokenObj.getExpireIn();

            session.setAttribute("demo_access_token", accessToken);
            session.setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

            // 通过accessToken获取openID
            OpenID openIDObj = new OpenID(accessToken);
            openId = openIDObj.getUserOpenID();

            // 通过accessToken和openID获取用户qzone信息
            UserInfo qzoneUserInfo = new UserInfo(accessToken, openId);
            UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();

            if (userInfoBean.getRet() == 0) { // 成功获取到用户qzone信息
                User user = new User();
                user.setOpenId(openId);
                user.setNickname(removeNonBmpUnicode(userInfoBean.getNickname()));
                ServerResponse<UserVo> resp = userService.qqLogin(user);
                if (resp.isSuccess()) { // 登陆或快速注册成功
                    session.setAttribute(Const.CURRENT_USER, resp.getData());
                    return "redirect:/user/browse/home";
                } else { // 注册失败
                    session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, resp.getMsg());
                }
            } else { // 未获取到用户qzone信息
                session.setAttribute(Const.SessionAttributeCode.NOTICE_MSG, "很抱歉，我们没能正确获取到您的信息，原因是：" + userInfoBean.getMsg());
            }
        }
        session.setAttribute(Const.SessionAttributeCode.NOTICE_TITLE, "点此返回");
        session.setAttribute(Const.SessionAttributeCode.NOTICE_BACK, "user/browse/jump?jump=login");
        return "redirect:/user/browse/jump?jump=notice";
    }

    // 移除QQ用户昵称中的表情
    public String removeNonBmpUnicode(String str) {
        if (str == null) {
            return null;
        }
        str = str.replaceAll("[^\\u0000-\\uFFFF]", "");
        if ("".equals(str)) {
            str = "($ _ $)";
        }
        return str;
    }
}