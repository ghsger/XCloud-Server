package cn.zf233.xcloud.service.impl;

import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.mapper.FileMapper;
import cn.zf233.xcloud.mapper.UserMapper;
import cn.zf233.xcloud.service.AdminService;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.vo.AdminVo;
import cn.zf233.xcloud.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zf233 on 2021/1/17
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Override
    public ServerResponse getAllUserInfo() {
        List<User> users = userMapper.selectUsers();
        List<AdminVo> adminVos = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        AdminVo adminVo;
        for (User user : users) {
            adminVo = new AdminVo();

            adminVo.setId(user.getId());
            adminVo.setEmail(StringUtils.isBlank(user.getEmail()) ? "QQ用户" : user.getEmail());
            adminVo.setUsername(user.getUsername());
            adminVo.setNickname(user.getNickname());
            adminVo.setRole(user.getRole());
            adminVo.setLevel(user.getLevel());
            adminVo.setUseCapacity(userService.getUseCapacityOfUserId(user.getId()));
            adminVo.setCapacity(user.getLevel() * 10);
            adminVo.setGrowthValue(user.getGrowthValue());
            adminVo.setCreateTime(sdf.format(user.getCreateTime()));

            adminVos.add(adminVo);
        }

        return ServerResponse.createBySuccess("获取成功", adminVos);
    }

    @Override
    public ServerResponse adminLogin(User user) {
        ServerResponse<UserVo> response = userService.login(user);
        if (response.isSuccess()) {

            if (response.getData().getRole() == 2) {
                return ServerResponse.createBySuccess(response.getData());
            }
            return ServerResponse.createByErrorMessage("您不是管理员");
        }
        return response;
    }

    @Override
    public ServerResponse updateUserRole(User user) {
        Integer flag = userMapper.updateByPrimaryKeySelective(user);
        if (flag > 0) {
            return ServerResponse.createBySuccessMessage("修改成功");
        }
        return ServerResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServerResponse removeUser(User user) {
        User userInfo = userMapper.selectByPrimaryKey(user.getId());
        File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(userInfo.getId());

        ServerResponse serverResponse = fileService.removeFileOrFolder(new Integer[]{rootNode.getId()}, userInfo);
        if (serverResponse.isSuccess()) {

            userMapper.deleteByPrimaryKey(userInfo.getId());
            if (StringUtils.isNotBlank(userInfo.getEmail())) {

                try {
                    userService.sendEmail(userInfo.getEmail(),
                            "XCloud 用户提醒",
                            userInfo.getNickname(),
                            "因使用违规，您的账号" + userInfo.getUsername() + "已经被XCloud自动移除，XCloud致力于保护您的隐私，您的满意是我们前进的动力。", "https://www.xcloud.show");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            return ServerResponse.createBySuccessMessage("用户移除成功");
        }

        return serverResponse;
    }
}
