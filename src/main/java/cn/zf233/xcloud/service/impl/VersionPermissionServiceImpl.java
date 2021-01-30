package cn.zf233.xcloud.service.impl;

import cn.zf233.xcloud.entity.VersionPermission;
import cn.zf233.xcloud.mapper.VersionPermissionMapper;
import cn.zf233.xcloud.service.VersionPermissionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zf233 on 2020/12/15
 */
@Service
public class VersionPermissionServiceImpl implements VersionPermissionService {

    @Resource
    private VersionPermissionMapper versionPermissionMapper;

    // 获取App版本号
    public VersionPermission getVersionPermission() {
        return versionPermissionMapper.selectByPrimaryKey(1);
    }

    // 测试App版本号是否一致
    @Override
    public Boolean testVersionCodeOfUserRequest(VersionPermission serviceVersionPermission) {
        VersionPermission versionPermission = getVersionPermission();
        if (versionPermission == null || serviceVersionPermission == null) {
            return true;
        }

        String appVersionCode = versionPermission.getCode();
        String serviceCode = serviceVersionPermission.getCode();
        if (StringUtils.isBlank(appVersionCode) || StringUtils.isBlank(serviceCode)) {
            return true;
        }

        return !appVersionCode.equals(serviceCode);
    }

    // 测试App版本号是否一致
    @Override
    public Boolean testVersionCodeOfFileRequest(String appVersionCode) {
        VersionPermission serviceVersionPermission = getVersionPermission();
        if (serviceVersionPermission == null) {
            return true;
        }

        String serviceCode = serviceVersionPermission.getCode();
        if (StringUtils.isBlank(appVersionCode) || StringUtils.isBlank(serviceCode)) {
            return true;
        }

        return !appVersionCode.equals(serviceCode);
    }
}
