package cn.zf233.xcloud.service;

import cn.zf233.xcloud.entity.VersionPermission;

/**
 * Created by zf233 on 2020/12/15
 */
public interface VersionPermissionService {
    Boolean testVersionCodeOfUserRequest(VersionPermission versionPermission);

    Boolean testVersionCodeOfFileRequest(String appVersionCode);
}
