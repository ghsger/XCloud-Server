package cn.zf233.xcloud.commom;

/**
 * Created by zf233 on 2020/11/27
 */
public enum  ResponseCodeENUM {

    SUCCESS(200, "SUCCESS"),
    ERROR(500, "ERROR"),
    NEED_LOGIN(401, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(400, "ILLEGAL_ARGUMENT"),
    VERSION_FAILURE(700, "VERSION_FAILURE");

    private final int code;
    private final String desc;


    ResponseCodeENUM(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
