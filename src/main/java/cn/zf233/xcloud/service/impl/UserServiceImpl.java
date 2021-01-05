package cn.zf233.xcloud.service.impl;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.mapper.FileMapper;
import cn.zf233.xcloud.mapper.UserMapper;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.util.RedisUtil;
import cn.zf233.xcloud.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zf233 on 2020/12/25
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private FileService fileService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RedisUtil filePermissionRedisUtil;

    @Override
    public ServerResponse<UserVo> login(User user) {
        if (checkUserDetail(user)) {
            return ServerResponse.createByErrorIllegalArgument("用户信息有误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + user.getPassword()).getBytes()));

        User userOfLogin = redisUtil.readUser(user);
        if (userOfLogin == null) {
            userOfLogin = userMapper.selectUserByUsernameAndPassword(user);
            if (userOfLogin == null) {
                return ServerResponse.createByErrorMessage("登陆失败");
            }
            redisUtil.saveUser(userOfLogin);
        }

        return ServerResponse.createBySuccess("登陆成功", assembleUserVoDetail(userOfLogin));
    }

    @Override
    @Transactional
    public ServerResponse<UserVo> update(User user) {
        if (checkUserDetail(user)) {
            return ServerResponse.createByErrorIllegalArgument("更新信息格式有误");
        }
        if (userMapper.selectByPrimaryKey(user.getId()) == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        user.setUsername(null);
        user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + user.getPassword()).getBytes()));
        int resultFlag = userMapper.updateByPrimaryKeySelective(user);
        if (resultFlag > 0) {
            User userOfUpdate = userMapper.selectByPrimaryKey(user.getId());
            redisUtil.saveUser(userOfUpdate);
            return ServerResponse.createBySuccess("更新成功", assembleUserVoDetail(userOfUpdate));
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    @Override
    @Transactional
    public ServerResponse<UserVo> regist(User user, String inviteCode) {
        if (!Const.INVITE_CODE.equals(inviteCode)) {
            return ServerResponse.createByErrorIllegalArgument("邀请码有误");
        }
        if (checkUserDetail(user)) {
            return ServerResponse.createByErrorIllegalArgument("注册信息格式有误");
        }
        User userOfRegist = userMapper.selectUserByUsername(user.getUsername());
        if (userOfRegist != null) {
            return ServerResponse.createByErrorMessage("用户已存在");
        }
        user.setNickname(StringUtils.isBlank(user.getNickname()) ? user.getUsername() : user.getNickname());
        user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + user.getPassword()).getBytes()));
        user.setRole(0);
        user.setLevel(1);
        user.setGrowthValue(0);
        user.setCreateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        user.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        Integer resultFlag = userMapper.insert(user);
        if (resultFlag > 0) {
            userOfRegist = userMapper.selectUserByUsername(user.getUsername());
            userOfRegist = userMapper.selectByPrimaryKey(userOfRegist.getId());
            File rootNode = new File();
            rootNode.setUserId(userOfRegist.getId());
            rootNode.setParentId(-1);
            rootNode.setFolder(1);
            rootNode.setRemark("根节点");
            rootNode.setUploadTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            rootNode.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            fileMapper.insert(rootNode);
            redisUtil.saveUser(userOfRegist);
            return ServerResponse.createBySuccess("注册成功", assembleUserVoDetail(userOfRegist));
        }
        return ServerResponse.createByErrorMessage("注册失败");
    }

    @Override
    @Transactional
    public Integer getUseCapacityOfUserId(Integer userId) {
        int countOfFile = 0;
        File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(userId);
        Set<File> filesSet = new HashSet<>();
        fileService.findChildParentId(filesSet, rootNode.getId(), userId);
        for (File fileFor : filesSet) {
            if (fileFor.getFolder() == 0) {
                countOfFile++;
            }
        }
        return countOfFile;
    }

    @Override
    public UserVo getUserByPrimarykey(Integer userid) {
        return assembleUserVoDetail(userMapper.selectByPrimaryKey(userid));
    }

    @Override
    @Transactional
    public void updateUserGrowthValueByPrimaryKeyTask(Integer userId) {
        User targetUser = userMapper.selectByPrimaryKey(userId);
        targetUser.setGrowthValue(targetUser.getGrowthValue() + 1);
        userMapper.updateByPrimaryKeySelective(targetUser);
    }

    @Override
    public void refreshUserLevelTask() {
        List<User> users = userMapper.selectUsers();
        for (User user : users) {
            int userLevel = (user.getGrowthValue() - 1) / 100 + 1;
            if (userLevel != user.getLevel()) {
                user.setLevel(userLevel);
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
    }

    @Override
    public void clearUserLoginDetailTask() {
        filePermissionRedisUtil.clearAllUserLoginDetail(userMapper.selectUsers());
    }

    private UserVo assembleUserVoDetail(User targetUser) {
        UserVo targetUserVo = new UserVo();
        targetUserVo.setId(targetUser.getId());
        targetUserVo.setUsername(targetUser.getUsername());
        if (StringUtils.isNotBlank(targetUser.getNickname())) {
            targetUserVo.setUsername(targetUser.getNickname());
        }
        targetUserVo.setRole(targetUser.getRole());
        targetUserVo.setLevel(targetUser.getLevel());
        targetUserVo.setGrowthValue(targetUser.getGrowthValue());
        Integer useCapacityForRedis = redisUtil.readUserUseCapacity(targetUser);
        if (useCapacityForRedis != -1) {
            targetUserVo.setUseCapacity(useCapacityForRedis);
        } else {
            Integer useCapacityOfUserId = getUseCapacityOfUserId(targetUser.getId());
            targetUserVo.setUseCapacity(useCapacityOfUserId);
            redisUtil.saveUserUseCapacity(targetUser, useCapacityOfUserId);
        }
        return targetUserVo;
    }

    private Boolean checkUserDetail(User targetUser) {
        if (targetUser == null) {
            return true;
        }
        if (StringUtils.isBlank(targetUser.getUsername()) || StringUtils.isBlank(targetUser.getPassword())) {
            return true;
        }
        return targetUser.getUsername().trim().length() < 5 || targetUser.getPassword().trim().length() < 5;
    }
}
