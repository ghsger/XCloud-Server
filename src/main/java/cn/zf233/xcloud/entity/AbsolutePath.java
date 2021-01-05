package cn.zf233.xcloud.entity;

/**
 * Created by zf233 on 2020/12/27
 */
public class AbsolutePath {

    private Integer parentid;
    private String folderName;

    public AbsolutePath() {
    }

    public AbsolutePath(Integer parentid, String folderName) {
        this.parentid = parentid;
        this.folderName = folderName;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
