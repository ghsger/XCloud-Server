package cn.zf233.xcloud.service;

import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.vo.UserVo;


/**
 * Created by zf233 on 2020/11/4
 */
public interface UserService {

    ServerResponse<UserVo> login(User user);

    ServerResponse<UserVo> update(User user);

    ServerResponse<UserVo> regist(User user, String inviteCode);

    Integer getUseCapacityOfUserId(Integer userId);

    UserVo getUserByPrimarykey(Integer userid);

    void updateUserGrowthValueByPrimaryKeyTask(Integer id);

    void refreshUserLevelTask();

    void clearUserLoginDetailTask();
}
