package cn.zf233.xcloud.entity;

import java.io.Serializable;

public class File implements Serializable {

    private static final long serialVersionUID = 8857945259217487581L;

    private Integer id;

    private Integer userId;

    private Integer parentId;

    private Integer folder;

    private String randomFileName;

    private String oldFileName;

    private Long fileSize;

    private String fileType;

    private Integer classify;

    private String remark;

    private Long uploadTime;

    private Long updateTime;

    public File() {
    }

    public File(Integer id, Integer userId, Integer parentId, Integer folder, String randomFileName, String oldFileName, Long fileSize, String fileType, Integer classify, String remark, Long uploadTime, Long updateTime) {
        this.id = id;
        this.userId = userId;
        this.parentId = parentId;
        this.folder = folder;
        this.randomFileName = randomFileName;
        this.oldFileName = oldFileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.classify = classify;
        this.remark = remark;
        this.uploadTime = uploadTime;
        this.updateTime = updateTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getFolder() {
        return folder;
    }

    public void setFolder(Integer folder) {
        this.folder = folder;
    }

    public String getRandomFileName() {
        return randomFileName;
    }

    public void setRandomFileName(String randomFileName) {
        this.randomFileName = randomFileName;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getClassify() {
        return classify;
    }

    public void setClassify(Integer classify) {
        this.classify = classify;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}