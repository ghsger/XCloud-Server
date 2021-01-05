package cn.zf233.xcloud.commom;

import cn.zf233.xcloud.entity.AbsolutePath;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zf233 on 2020/11/27
 */
//保证序列化json的时候,如果是null的对象,key也会消失
@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private static final long serialVersionUID = -8020751417469577331L;
    private Integer status;
    private String msg;
    private List<AbsolutePath> absolutePath;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data, List<AbsolutePath> absolutePath) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.absolutePath = absolutePath;
    }

    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess() {
        return this.status == ResponseCodeENUM.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public List<AbsolutePath> getAbsolutePath() {
        return absolutePath;
    }

    public static <T> ServerResponse<T> createbysuccess() {
        return new ServerResponse<>(ResponseCodeENUM.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<>(ResponseCodeENUM.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<>(ResponseCodeENUM.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccessAbsolutePathMsg(String msg, T data, List<AbsolutePath> absolutePath) {
        return new ServerResponse<>(ResponseCodeENUM.SUCCESS.getCode(), msg, data, absolutePath);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCodeENUM.SUCCESS.getCode(), msg, data);
    }


    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<>(ResponseCodeENUM.ERROR.getCode(), ResponseCodeENUM.ERROR.getDesc());
    }


    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
        return new ServerResponse<>(ResponseCodeENUM.ERROR.getCode(), errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorNeedLogin() {
        return new ServerResponse<>(ResponseCodeENUM.NEED_LOGIN.getCode(), ResponseCodeENUM.NEED_LOGIN.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorIllegalArgument() {
        return new ServerResponse<>(ResponseCodeENUM.ILLEGAL_ARGUMENT.getCode(), ResponseCodeENUM.ILLEGAL_ARGUMENT.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorIllegalArgument(String errorMessage) {
        return new ServerResponse<>(ResponseCodeENUM.ILLEGAL_ARGUMENT.getCode(), errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
        return new ServerResponse<>(errorCode, errorMessage);
    }
}
