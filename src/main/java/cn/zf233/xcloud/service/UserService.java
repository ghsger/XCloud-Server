package cn.zf233.xcloud.service;

import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.vo.UserVo;

import javax.mail.MessagingException;


/**
 * Created by zf233 on 2020/11/4
 */
public interface UserService {

    ServerResponse<UserVo> login(User user);

    ServerResponse<UserVo> qqLogin(User user);

    ServerResponse<UserVo> update(User user);

    ServerResponse<UserVo> regist(User user);

    ServerResponse checkRegistUser(User user, String UUID);

    ServerResponse againSendUserRegistEmail(User user);

    Integer getUseCapacityOfUserId(Integer userId);

    UserVo getUserVoByPrimarykey(Integer userid);

    void updateUserGrowthValueByPrimaryKey(Integer id);

    void refreshUserLevelTask();

    void removeUserInfoOfRegistFailTask();

    void sendCodeForUserRegist(String to,
                               String title,
                               String nickname,
                               String contentOfPage,
                               String url) throws MessagingException;

    ServerResponse checkUserInfoExists(User user);
}
