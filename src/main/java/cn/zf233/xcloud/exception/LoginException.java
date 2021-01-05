package cn.zf233.xcloud.exception;

/**
 * Created by zf233 on 2020/11/4
 */
public class LoginException extends RuntimeException {
    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }
}
