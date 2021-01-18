package cn.zf233.xcloud.service;

import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;

/**
 * Created by zf233 on 2021/1/17
 */
public interface AdminService {

    ServerResponse getAllUserInfo();
    ServerResponse adminLogin(User user);
    ServerResponse updateUserRole(User user);
    ServerResponse removeUser(User user);

}
