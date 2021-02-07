package cn.zf233.xcloud.service.impl;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.mapper.FileMapper;
import cn.zf233.xcloud.mapper.UserMapper;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.util.EmailUtil;
import cn.zf233.xcloud.util.RedisUtil;
import cn.zf233.xcloud.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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
    private EmailUtil emailUtil;

    @Resource
    private TemplateEngine templateEngine;

    // 登陆
    @Override
    public ServerResponse<UserVo> login(User user) {
        if (checkUserInfo(user)) {
            return ServerResponse.createByErrorIllegalArgument("用户信息有误");
        }

        user.setEmail(user.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + user.getPassword()).getBytes()));

        User userInfo = userMapper.selectUserByUsernameAndPassword(user);
        if (userInfo == null) {
            userInfo = userMapper.selectUserByEmailAndPassword(user);
        }

        if (userInfo != null) {
            if (userInfo.getRole() == -1) {
                return ServerResponse.createByErrorCodeMessage(
                        Const.CheckEmailENUM.NOT_CHECK.getCode(),
                        "登陆失败(已注册-邮箱未验证)",
                        assembleUserVoDetail(userInfo));
            }

            if (userInfo.getRole() == 1) {
                return ServerResponse.createByErrorMessage("账户被锁定");
            }

            return ServerResponse.createBySuccess("登陆成功", assembleUserVoDetail(userInfo));
        }

        return ServerResponse.createByErrorMessage("登陆失败");
    }

    // QQ登陆or注册
    @Override
    @Transactional
    public ServerResponse<UserVo> qqLogin(User user) {
        User userInfo = userMapper.selectUserByOpenId(user.getOpenId());
        if (userInfo == null) {

            do {
                int no = new Random().nextInt(900) + 100;
                user.setUsername("qq" + no);
            } while (userMapper.selectUserByUsername(user.getUsername()) != null);
            user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + "xcloud").getBytes()));
            user.setRole(0);
            user.setLevel(1);
            user.setGrowthValue(0);
            user.setCreateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            user.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());

            if (StringUtils.isNotBlank(user.getHeadUrl())) {
                if (!user.getHeadUrl().contains("https")) {
                    if (user.getHeadUrl().contains("http")) {
                        user.setHeadUrl(user.getHeadUrl().replace("http", "https"));
                    }
                }
            }

            if (StringUtils.isBlank(user.getHeadUrl())) {
                user.setHeadUrl(Const.DEFAULT_USER_HEAD_URL);
            }

            int flagOfInsert = userMapper.insert(user);
            if (flagOfInsert > 0) {
                File file = assembleRootNodeOfRegistUser(user);
                fileMapper.insert(file);
                return ServerResponse.createBySuccess("注册成功", assembleUserVoDetail(userMapper.selectUserByOpenId(user.getOpenId())));
            }

            return ServerResponse.createByErrorMessage("注册失败");
        }

        if (userInfo.getRole() == 1) {

            return ServerResponse.createByErrorMessage("账户被锁定");
        }

        if (StringUtils.isNotBlank(userInfo.getHeadUrl()) && StringUtils.isNotBlank(user.getHeadUrl())) {
            if (!userInfo.getHeadUrl().equals(user.getHeadUrl())) {
                userInfo.setHeadUrl(user.getHeadUrl());
                userMapper.updateByPrimaryKeySelective(userInfo);
            }
        }

        return ServerResponse.createBySuccess("登陆成功", assembleUserVoDetail(userInfo));
    }

    // 更新信息
    @Override
    @Transactional
    public ServerResponse<UserVo> update(User user) {
        if (checkUserInfo(user)) {
            return ServerResponse.createByErrorIllegalArgument("更新信息格式有误");
        }

        if (userMapper.selectByPrimaryKey(user.getId()) == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        user.setEmail(null);
        user.setUsername(null);
        user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + user.getPassword()).getBytes()));
        user.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());

        int resultFlag = userMapper.updateByPrimaryKeySelective(user);
        if (resultFlag > 0) {
            User userInfo = userMapper.selectByPrimaryKey(user.getId());

            return ServerResponse.createBySuccess("更新成功", assembleUserVoDetail(userInfo));
        }

        return ServerResponse.createByErrorMessage("更新失败");
    }

    // 注册
    @Override
    @Transactional
    public ServerResponse<UserVo> regist(User user) {
        if (checkUserInfo(user)) {
            return ServerResponse.createByErrorIllegalArgument("注册信息格式有误");
        }

        if (StringUtils.isBlank(user.getEmail())) {
            return ServerResponse.createByErrorMessage("邮箱不可为空");
        }

        if (userMapper.selectUserByEmail(user.getEmail()) != null) {
            return ServerResponse.createByErrorMessage("邮箱已被占用");
        }

        if (userMapper.selectUserByUsername(user.getUsername()) != null) {
            return ServerResponse.createByErrorMessage("用户已存在");
        }

        user.setHeadUrl(Const.DEFAULT_USER_HEAD_URL);
        user.setNickname(StringUtils.isBlank(user.getNickname()) ? user.getUsername() : user.getNickname());
        user.setPassword(DigestUtils.md5DigestAsHex((Const.DOMAIN_NAME + user.getPassword()).getBytes()));
        user.setRole(-1);
        user.setLevel(1);
        user.setGrowthValue(0);
        user.setCreateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        user.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());

        int resultFlag = userMapper.insert(user);
        if (resultFlag > 0) {

            File rootNode = assembleRootNodeOfRegistUser(user);
            fileMapper.insert(rootNode);

            String uuid = UUID.randomUUID().toString();
            redisUtil.set(Const.USER_UUID_KEY_PREFIX + user.getId(), uuid);
            try {

                sendEmail(user.getEmail(),
                        "XCloud 邮箱验证",
                        user.getNickname(),
                        "点我以完成XCloud账号注册，XCloud致力于保护您的隐私，您的满意是我们前进的动力。",
                        "https://www.xcloud.show/xcloud/user/browse/check?id=" + user.getId() + "&" + "uuid=" + uuid);
            } catch (MessagingException e) {
                e.printStackTrace();

                return ServerResponse.createByErrorMessage("注册失败");
            }

            return ServerResponse.createBySuccess("已发送注册邮件,有效期 180 秒", assembleUserVoDetail(user));
        }

        return ServerResponse.createByErrorMessage("注册失败");
    }

    // 检查验证邮件是否正确
    @Override
    @Transactional
    public ServerResponse checkRegistUser(User user, String UUID) {
        if (user.getId() == null || user.getId() < 0) {
            return ServerResponse.createByErrorMessage("用户ID有误");
        }

        User userInfo = userMapper.selectByPrimaryKey(user.getId());
        if (userInfo == null) {
            return ServerResponse.createByErrorCodeMessage(
                    Const.CheckEmailENUM.USER_NOT_EXISTS.getCode(),
                    "邮箱验证超时用户信息已移除或不存在的用户(请重新注册)");
        }

        if (userInfo.getRole() != -1) {
            return ServerResponse.createByErrorCodeMessage(
                    Const.CheckEmailENUM.ALREADY_CHECK.getCode(),
                    "已验证,请不要重复验证");
        }

        String redisKey = Const.USER_UUID_KEY_PREFIX + user.getId();
        Long registUserUUIDTimeOut = redisUtil.getTimeOutOfKey(redisKey);
        if (registUserUUIDTimeOut > 0) {

            String registUserUUID = redisUtil.get(redisKey);
            if (StringUtils.isNotBlank(registUserUUID) && StringUtils.isNotBlank(UUID)) {

                if (UUID.equals(registUserUUID)) {

                    userInfo.setRole(0);
                    int registFlag = userMapper.updateByPrimaryKeySelective(userInfo);
                    if (registFlag > 0) {
                        redisUtil.remove(redisKey);
                        return ServerResponse.createBySuccessMessage("邮箱验证成功(注册成功)");
                    }

                    return ServerResponse.createBySuccessMessage("注册失败");
                }
            }
        }

        return ServerResponse.createByErrorCodeMessage(Const.CheckEmailENUM.TIME_OUT.getCode(), "邮箱验证超时");
    }

    // 重新发送验证邮件
    @Override
    public ServerResponse againSendUserRegistEmail(User user) {
        User userInfo = userMapper.selectByPrimaryKey(user.getId());
        if (userInfo == null) {
            return ServerResponse.createByErrorCodeMessage(
                    Const.CheckEmailENUM.USER_NOT_EXISTS.getCode(),
                    "邮箱验证超时用户信息已移除或不存在的用户(请重新注册)");
        }

        if (userInfo.getRole() == 0) {
            return ServerResponse.createByErrorCodeMessage(
                    Const.CheckEmailENUM.ALREADY_CHECK.getCode(),
                    "已验证,请不要重复发送验证邮件");
        }

        String redisKey = Const.USER_UUID_KEY_PREFIX + user.getId();
        Long timeOut = redisUtil.getTimeOutOfKey(redisKey);
        if (timeOut - 5 > 0) {
            return ServerResponse.createByErrorCodeMessage(
                    Const.CheckEmailENUM.UUID_EXISTS.getCode(),
                    "发送的太频繁," + timeOut + "秒后继续");
        }

        redisUtil.remove(redisKey);
        String uuid = UUID.randomUUID().toString();
        redisUtil.set(redisKey, uuid);

        try {
            sendEmail(userInfo.getEmail(),
                    "XCloud 邮箱验证",
                    userInfo.getNickname(),
                    "点我以完成XCloud账号注册，XCloud致力于保护您的隐私，您的满意是我们前进的动力。",
                    "https://www.xcloud.show/xcloud/user/browse/check?id=" + userInfo.getId() + "&" + "uuid=" + uuid);
        } catch (MessagingException e) {
            e.printStackTrace();

            return ServerResponse.createByErrorMessage("邮箱验证链接发送失败");
        }

        return ServerResponse.createBySuccessMessage("已发送注册邮件,有效期 180 秒");
    }

    // 获取已用容量
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

    // 获取用户展示对象
    @Override
    public UserVo getUserVoByPrimarykey(Integer userId) {
        User user = new User();
        user.setId(userId);
        user = userMapper.selectByPrimaryKey(userId);
        return assembleUserVoDetail(user);
    }

    // 更新用户成长值
    @Override
    @Transactional
    public void updateUserGrowthValueByPrimaryKey(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        user.setGrowthValue(user.getGrowthValue() + 1);
        user.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        userMapper.updateByPrimaryKeySelective(user);
    }

    // 刷新用户等级
    @Override
    @Transactional
    public void refreshUserLevelTask() {
        List<User> users = userMapper.selectUsers();
        for (User user : users) {

            int userLevel = (user.getGrowthValue() - 1) / 100 + 1;
            if (userLevel != user.getLevel()) {
                user.setLevel(userLevel);
                user.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
    }

    // 清除系统使用的redis缓存
    @Override
    @Transactional
    public void removeUserInfoOfRegistFailTask() {
        List<User> users = userMapper.selectUsers();
        for (User user : users) {

            if (user.getRole() == -1) {
                redisUtil.remove(Const.USER_UUID_KEY_PREFIX + user.getId());

                try {
                    sendEmail(user.getEmail(),
                            "XCloud 用户提醒",
                            user.getNickname(),
                            "您的邮箱仍未验证，系统已安全的移除您的信息，XCloud致力于保护您的隐私，您的满意是我们前进的动力。",
                            "https://www.xcloud.show");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                File file = fileMapper.selectRootNodeOfUserByPrimaryKey(user.getId());
                if (file != null) {
                    fileMapper.deleteByPrimaryKey(file.getId());
                }
                userMapper.deleteByPrimaryKey(user.getId());
            }
        }
    }

    // 异步发送邮箱验证邮件
    public void sendEmail(String to,
                          String title,
                          String nickname,
                          String contentOfPage,
                          String url) throws MessagingException {

        Context context = new Context();

        context.setVariable("title", title);
        context.setVariable("nickname", nickname);
        context.setVariable("content", contentOfPage);
        context.setVariable("url", url);

        String content = templateEngine.process("email", context);

        emailUtil.send(to, "XCloud 提示邮件", content);
    }

    @Override
    public ServerResponse checkUserInfoExists(User user) {
        User userInfo = null;
        if (StringUtils.isNotBlank(user.getUsername())) {
            userInfo = userMapper.selectUserByUsername(user.getUsername());
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            userInfo = userMapper.selectUserByEmail(user.getEmail());
        }

        if (userInfo == null) {
            return ServerResponse.createBySuccess("允许使用此用户名或密码");
        }

        return ServerResponse.createByErrorMessage("不允许使用此用户名或密码");
    }

    // 组装用户展示对象
    private UserVo assembleUserVoDetail(User user) {
        UserVo userVo = new UserVo();

        userVo.setHeadUrl(user.getHeadUrl());
        userVo.setId(user.getId());
        userVo.setEmail(user.getEmail());
        userVo.setUsername(user.getUsername());

        if (StringUtils.isNotBlank(user.getNickname())) {
            userVo.setUsername(user.getNickname());
        }

        userVo.setRole(user.getRole());
        userVo.setLevel(user.getLevel());
        userVo.setGrowthValue(user.getGrowthValue());

        int useCapacityOfUserId = getUseCapacityOfUserId(user.getId());
        userVo.setUseCapacity(useCapacityOfUserId);

        return userVo;
    }

    // 组装用户注册对象
    private File assembleRootNodeOfRegistUser(User userInfo) {
        File rootNode = new File();

        rootNode.setUserId(userInfo.getId());
        rootNode.setParentId(-1);
        rootNode.setFolder(1);
        rootNode.setRemark("根节点");
        rootNode.setUploadTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        rootNode.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());

        return rootNode;
    }

    // 检查用户细节
    private Boolean checkUserInfo(User user) {
        if (user == null) {
            return true;
        }

        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return true;
        }

        return user.getUsername().trim().length() < 5 || user.getPassword().trim().length() < 5;
    }
}
