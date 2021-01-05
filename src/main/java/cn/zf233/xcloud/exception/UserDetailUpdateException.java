package cn.zf233.xcloud.exception;

/**
 * Created by zf233 on 2020/11/4
 */
public class UserDetailUpdateException extends RuntimeException {
    public UserDetailUpdateException() {
    }

    public UserDetailUpdateException(String message) {
        super(message);
    }
}
