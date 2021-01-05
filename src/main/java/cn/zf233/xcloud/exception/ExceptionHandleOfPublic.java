package cn.zf233.xcloud.exception;

import cn.zf233.xcloud.commom.Const;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpSession;

/**
 * Created by zf233 on 2020/11/4
 */
@ControllerAdvice
public class ExceptionHandleOfPublic {

    @ExceptionHandler(value = LoginException.class)
    public String doLoginException(HttpSession session, Exception exception) {
        session.setAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        session.setAttribute(Const.SessionAttributeCode.ERROR_BACK, "pages/user/login.jsp");
        return "redirect:/pages/error/error.jsp";
    }

    @ExceptionHandler(value = UserDetailUpdateException.class)
    public String doUserDetailUpdateException(HttpSession session, Exception exception) {
        session.setAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        session.setAttribute(Const.SessionAttributeCode.ERROR_BACK, "pages/user/userDetails.jsp");
        return "redirect:/pages/error/error.jsp";
    }

    @ExceptionHandler(value = RegistException.class)
    public String doRegistException(HttpSession session, Exception exception) {
        session.setAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        session.setAttribute(Const.SessionAttributeCode.ERROR_BACK, "pages/user/regist.jsp");
        return "redirect:/pages/error/error.jsp";
    }

    @ExceptionHandler(value = Exception.class)
    public String doException(HttpSession session, Exception exception) {
        session.setAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        session.setAttribute(Const.SessionAttributeCode.ERROR_BACK, "user/browse/index80");
        return "redirect:/pages/error/error.jsp";
    }

}

