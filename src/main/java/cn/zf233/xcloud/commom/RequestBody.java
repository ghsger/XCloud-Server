package cn.zf233.xcloud.commom;

import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.entity.VersionPermission;

/**
 * Created by zf233 on 2020/11/28
 */
public class RequestBody {

    private User user;
    private Integer sortFlag;
    private Integer sortType;
    private String matchCode;
    private String inviteCode;
    private Integer parentid;
    private VersionPermission versionPermission;

    public RequestBody() {
    }

    public RequestBody(User user, Integer sortFlag, Integer sortType, String matchCode, String inviteCode, Integer parentid, VersionPermission versionPermission) {
        this.user = user;
        this.sortFlag = sortFlag;
        this.sortType = sortType;
        this.matchCode = matchCode;
        this.inviteCode = inviteCode;
        this.parentid = parentid;
        this.versionPermission = versionPermission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(Integer sortFlag) {
        this.sortFlag = sortFlag;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public VersionPermission getVersionPermission() {
        return versionPermission;
    }

    public void setVersionPermission(VersionPermission versionPermission) {
        this.versionPermission = versionPermission;
    }
}