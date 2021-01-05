package cn.zf233.xcloud.vo;

/**
 * Created by zf233 on 2020/11/27
 */
public class FileVo {
    private Integer id;
    private Integer parentId;
    private Integer folder;
    private String fileName;
    private String fileType;
    private String fileSize;
    private String uploadTime;
    private String remark;
    private Integer downloadCount;

    public FileVo() {
    }

    public FileVo(Integer id, Integer parentId, Integer folder, String fileName, String fileType, String fileSize, String uploadTime, String remark, Integer downloadCount) {
        this.id = id;
        this.parentId = parentId;
        this.folder = folder;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.remark = remark;
        this.downloadCount = downloadCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
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
}

