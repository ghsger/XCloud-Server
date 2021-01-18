package cn.zf233.xcloud.exception;

import cn.zf233.xcloud.commom.Const;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by zf233 on 2020/11/4
 */
@ControllerAdvice
public class ExceptionHandleOfPublic {

    @ExceptionHandler(value = LoginException.class)
    public String doLoginException(Model model, Exception exception) {
        model.addAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        model.addAttribute(Const.SessionAttributeCode.ERROR_BACK, "login");
        return "error";
    }

    @ExceptionHandler(value = UserDetailUpdateException.class)
    public String doUserDetailUpdateException(Model model, Exception exception) {
        model.addAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        model.addAttribute(Const.SessionAttributeCode.ERROR_BACK, "user_detail");
        return "error";
    }

    @ExceptionHandler(value = RegistException.class)
    public String doRegistException(Model model, Exception exception) {
        model.addAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        model.addAttribute(Const.SessionAttributeCode.ERROR_BACK, "regist");
        return "error";
    }

    @ExceptionHandler(value = Exception.class)
    public String doException(Model model, Exception exception) {
        model.addAttribute(Const.SessionAttributeCode.ERROR_MSG, exception.getMessage());
        model.addAttribute(Const.SessionAttributeCode.ERROR_BACK, "home");
        return "error";
    }

}

