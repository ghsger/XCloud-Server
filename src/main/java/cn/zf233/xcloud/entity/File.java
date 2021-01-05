package cn.zf233.xcloud.entity;

import java.util.Objects;

public class File {
    private Integer id;

    private Integer userId;

    private Integer parentId;

    private Integer folder;

    private String groupName;

    private String remoteFilePath;

    private String oldFileName;

    private Long fileSize;

    private String fileType;

    private String remark;

    private Integer downloadCount;

    private String redisCacheName;

    private Long uploadTime;

    private Long updateTime;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getRedisCacheName() {
        return redisCacheName;
    }

    public void setRedisCacheName(String redisCacheName) {
        this.redisCacheName = redisCacheName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        File file = (File) o;
        return Objects.equals(id, file.id) && Objects.equals(userId, file.userId) && Objects.equals(parentId, file.parentId) && Objects.equals(folder, file.folder) && Objects.equals(groupName, file.groupName) && Objects.equals(remoteFilePath, file.remoteFilePath) && Objects.equals(oldFileName, file.oldFileName) && Objects.equals(fileSize, file.fileSize) && Objects.equals(fileType, file.fileType) && Objects.equals(remark, file.remark) && Objects.equals(downloadCount, file.downloadCount) && Objects.equals(redisCacheName, file.redisCacheName) && Objects.equals(uploadTime, file.uploadTime) && Objects.equals(updateTime, file.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, parentId, folder, groupName, remoteFilePath, oldFileName, fileSize, fileType, remark, downloadCount, redisCacheName, uploadTime, updateTime);
    }
}