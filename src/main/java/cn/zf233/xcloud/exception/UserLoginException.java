package cn.zf233.xcloud.exception;

/**
 * Created by zf233 on 2020/11/4
 */
public class UserLoginException extends LoginException {
    public UserLoginException() {
    }

    public UserLoginException(String message) {
        super(message);
    }
}
