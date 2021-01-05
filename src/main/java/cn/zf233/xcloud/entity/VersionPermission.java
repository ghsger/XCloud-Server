package cn.zf233.xcloud.entity;

/**
 * Created by zf233 on 2020/12/15
 */
public class VersionPermission {
    private Integer id;
    private String code;

    public VersionPermission() {
    }

    public VersionPermission(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
