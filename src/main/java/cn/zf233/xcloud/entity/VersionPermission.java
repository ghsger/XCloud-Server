package cn.zf233.xcloud.entity;

import java.io.Serializable;

/**
 * Created by zf233 on 2020/12/15
 */
public class VersionPermission implements Serializable {

    private static final long serialVersionUID = -7059663674119841998L;

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
