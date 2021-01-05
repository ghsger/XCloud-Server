package cn.zf233.xcloud.util;

import cn.zf233.xcloud.entity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by zf233 on 2020/11/27
 */
public class RedisUtil {

    @Resource
    private RedisTemplate<String, byte[]> writeRedis;

    @Resource
    private RedisTemplate<String, byte[]> readRedis;

    private static final String FILE_KEY_PREFIX = "zf233_file.";
    private static final String USER_USERNAME_PREFIX = "zf233_login.";
    private static final String USER_USE_CAPACITY_PREFIX = "zf233_user_use_capacity.";
    private static final String VERSION_PERMISSION = "zf233_version_permission";

    public String saveFile(byte[] fileBytes) {
        ValueOperations<String, byte[]> opsWrite = writeRedis.opsForValue();
        StringBuilder filename = new StringBuilder(FILE_KEY_PREFIX).append(System.currentTimeMillis());
        opsWrite.set(filename.toString(), fileBytes);
        return filename.toString();
    }

    public byte[] getFile(String redisCacheName) {
        ValueOperations<String, byte[]> opsRead = readRedis.opsForValue();
        byte[] bytes = opsRead.get(redisCacheName);
        if (bytes == null) {
            ValueOperations<String, byte[]> opsWrite = writeRedis.opsForValue();
            return opsWrite.get(redisCacheName);
        }
        return bytes;
    }

    public void removeFile(String redisCacheName) {
        writeRedis.delete(redisCacheName);
    }


    public User readUser(User user) {
        String key = USER_USERNAME_PREFIX + user.getId();
        ValueOperations<String, byte[]> opsForValue = readRedis.opsForValue();
        byte[] bytes = opsForValue.get(key);
        if (bytes != null) {
            String userJson = new String(bytes, StandardCharsets.UTF_8);
            User userOfRedis = JsonUtil.toObject(userJson, User.class);
            if (userOfRedis == null) {
                return null;
            }
            if (user.getUsername().equals(userOfRedis.getUsername()) && user.getPassword().equals(userOfRedis.getPassword())) {
                return userOfRedis;
            }
            return null;
        }
        return null;
    }

    public void saveUser(User user) {
        String key = USER_USERNAME_PREFIX + user.getId();
        writeRedis.delete(key);
        ValueOperations<String, byte[]> opsForValue = writeRedis.opsForValue();
        String userJson = JsonUtil.toJson(user);
        if (StringUtils.isNotBlank(userJson)) {
            opsForValue.set(key, userJson.getBytes(StandardCharsets.UTF_8));
        }
    }

    public Integer readUserUseCapacity(User user) {
        ValueOperations<String, byte[]> opsForValue = readRedis.opsForValue();
        String key = USER_USE_CAPACITY_PREFIX + user.getId();
        byte[] bytes = opsForValue.get(key);
        if (bytes != null) {
            String useCapacityString = new String(bytes, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(useCapacityString)) {
                return Integer.valueOf(useCapacityString);
            }
        }
        return -1;
    }

    public void updateAddUserUseCapacity(User user) {
        ValueOperations<String, byte[]> opsForValue = writeRedis.opsForValue();
        String key = USER_USE_CAPACITY_PREFIX + user.getId();
        byte[] bytes = opsForValue.get(key);
        if (bytes != null) {
            String useCapacityString = new String(bytes, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(useCapacityString)) {
                int useCapacity = Integer.parseInt(useCapacityString);
                this.saveUserUseCapacity(user, useCapacity + 1);
            }
        }
    }

    public void updateReduceUserUseCapacity(User user) {
        ValueOperations<String, byte[]> opsForValue = writeRedis.opsForValue();
        String key = USER_USE_CAPACITY_PREFIX + user.getId();
        byte[] bytes = opsForValue.get(key);
        if (bytes != null) {
            String useCapacityString = new String(bytes, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(useCapacityString)) {
                int useCapacity = Integer.parseInt(useCapacityString);
                if (useCapacity > 0) {
                    this.saveUserUseCapacity(user, useCapacity - 1);
                }
            }
        }
    }

    public void saveUserUseCapacity(User user, Integer useCapacity) {
        ValueOperations<String, byte[]> opsForValue = writeRedis.opsForValue();
        String key = USER_USE_CAPACITY_PREFIX + user.getId();
        writeRedis.delete(key);
        opsForValue.set(key, useCapacity.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void clearAllUserLoginDetail(List<User> users) {
        writeRedis.delete(VERSION_PERMISSION);
        for (User user : users) {
            writeRedis.delete(USER_USERNAME_PREFIX + user.getId());
            writeRedis.delete(USER_USE_CAPACITY_PREFIX + user.getId());
        }
    }

    public String readVersionPermission() {
        ValueOperations<String, byte[]> operations = readRedis.opsForValue();
        byte[] bytes = operations.get(VERSION_PERMISSION);
        if (bytes != null) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    public void saveVersionPermission(String versionPermissionCode) {
        ValueOperations<String, byte[]> operations = writeRedis.opsForValue();
        writeRedis.delete(VERSION_PERMISSION);
        operations.set(VERSION_PERMISSION, versionPermissionCode.getBytes(StandardCharsets.UTF_8));
    }
}
