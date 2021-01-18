package cn.zf233.xcloud.mapper;

import cn.zf233.xcloud.entity.User;

import java.util.List;

public interface UserMapper {
    User selectByPrimaryKey(Integer id);

    User selectUserByUsername(String username);

    User selectUserByEmail(String email);

    User selectUserByOpenId(String openId);

    User selectUserByUsernameAndPassword(User user);

    User selectUserByEmailAndPassword(User user);

    Integer insert(User user);

    Integer updateByPrimaryKeySelective(User user);

    List<User> selectUsers();

    void deleteByPrimaryKey(Integer id);
}