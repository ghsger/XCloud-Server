package cn.zf233.xcloud.util;

import cn.zf233.xcloud.entity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by zf233 on 2020/11/27
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, byte[]> redisTemplate;

    private static final String USER_KEY_PREFIX_ID = "zf233_user.id.";
    private static final String USER_KEY_PREFIX_USERNAME = "zf233_user.username.";
    private static final String USER_KEY_PREFIX_EMAIL = "zf233_user.email.";
    private static final String USER_KEY_PREFIX_OPENID = "zf233_user.openid.";
    private static final String USER_UUID_KEY_PREFIX = "zf233_user_regist_uuid.";
    private static final String USER_USE_CAPACITY_PREFIX = "zf233_user_capacity.";
    private static final String VERSION_PERMISSION = "zf233_version_permission";

    public User getUser(User user) {
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();
        String keyOfId = USER_KEY_PREFIX_ID + user.getId();
        byte[] bytes = ops.get(keyOfId);
        if (bytes == null) {
            String keyOfUsername = USER_KEY_PREFIX_USERNAME + user.getUsername();
            bytes = ops.get(keyOfUsername);
        }
        if (bytes == null) {
            if (StringUtils.isNotBlank(user.getEmail())) {
                String keyOfEmail = USER_KEY_PREFIX_EMAIL + user.getEmail();
                bytes = ops.get(keyOfEmail);
            }
        }
        if (bytes == null) {
            if (StringUtils.isNotBlank(user.getOpenId())) {
                String keyOfOpenId = USER_KEY_PREFIX_OPENID + user.getOpenId();
                bytes = ops.get(keyOfOpenId);
            }
        }
        if (bytes != null) {
            return JsonUtil.toObject(new String(bytes, StandardCharsets.UTF_8), User.class);
        }
        return null;
    }

    public void saveUser(User user) {
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();

        String keyOfId = USER_KEY_PREFIX_ID + user.getId();
        String keyOfUsername = USER_KEY_PREFIX_USERNAME + user.getUsername();
        String keyOfEmail = USER_KEY_PREFIX_EMAIL + user.getEmail();
        String keyOfOpenId = USER_KEY_PREFIX_OPENID + user.getOpenId();
        redisTemplate.delete(keyOfId);
        redisTemplate.delete(keyOfUsername);
        redisTemplate.delete(keyOfEmail);
        redisTemplate.delete(keyOfOpenId);

        String userOfJson = JsonUtil.toJson(user);
        if (StringUtils.isNotBlank(userOfJson)) {
            ops.set(keyOfId, userOfJson.getBytes(StandardCharsets.UTF_8));
            ops.set(keyOfUsername, userOfJson.getBytes(StandardCharsets.UTF_8));
            if (StringUtils.isNotBlank(user.getEmail())) {
                ops.set(keyOfEmail, userOfJson.getBytes(StandardCharsets.UTF_8));
            }
            if (StringUtils.isNotBlank(user.getOpenId())) {
                ops.set(keyOfOpenId, userOfJson.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public Integer getUserUseCapacity(Integer userId) {
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();
        String key = USER_USE_CAPACITY_PREFIX + userId;
        byte[] bytes = ops.get(key);
        if (bytes != null) {
            return Integer.valueOf(new String(bytes, StandardCharsets.UTF_8));
        }
        return -1;
    }

    public void saveUserUseCapacity(Integer userId, Integer useCapacity) {
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();
        String key = USER_USE_CAPACITY_PREFIX + userId;
        redisTemplate.delete(key);
        ops.set(key, useCapacity.toString().getBytes(StandardCharsets.UTF_8));
    }

    public String getVersionPermission() {
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();
        byte[] bytes = ops.get(VERSION_PERMISSION);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void saveVersionPermission(String versionPermissionCode) {
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();
        redisTemplate.delete(VERSION_PERMISSION);
        ops.set(VERSION_PERMISSION, versionPermissionCode.getBytes(StandardCharsets.UTF_8));
    }

    public void removeUserServerCache(User user) {

        String keyOfUserUseCapacity = USER_USE_CAPACITY_PREFIX + user.getId();
        redisTemplate.delete(keyOfUserUseCapacity);

        String keyOfId = USER_KEY_PREFIX_ID + user.getId();
        String keyOfUsername = USER_KEY_PREFIX_USERNAME + user.getUsername();
        String keyOfEmail = USER_KEY_PREFIX_EMAIL + user.getEmail();
        String keyOfOpenId = USER_KEY_PREFIX_OPENID + user.getOpenId();
        redisTemplate.delete(keyOfId);
        redisTemplate.delete(keyOfUsername);
        redisTemplate.delete(keyOfEmail);
        redisTemplate.delete(keyOfOpenId);
    }

    public void removeVersionPermission() {
        redisTemplate.delete(VERSION_PERMISSION);
    }

    public void removeRegistUserUUID(Integer userId) {
        String UUIDKey = USER_UUID_KEY_PREFIX + userId;
        redisTemplate.delete(UUIDKey);
    }

    public String getRegistUserUUID(Integer userId) {
        String UUIDKey = USER_UUID_KEY_PREFIX + userId;
        ValueOperations<String, byte[]> operations = redisTemplate.opsForValue();
        byte[] bytes = operations.get(UUIDKey);
        if (bytes != null) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    public void setRegistUserUUID(Integer userId, String UUID) {
        String UUIDKey = USER_UUID_KEY_PREFIX + userId;
        ValueOperations<String, byte[]> operations = redisTemplate.opsForValue();
        operations.set(UUIDKey, UUID.getBytes(StandardCharsets.UTF_8), 180, TimeUnit.SECONDS);
    }

    public Long getRegistUserUUIDTimeOut(Integer userId) {
        String UUIDKey = USER_UUID_KEY_PREFIX + userId;
        return redisTemplate.getExpire(UUIDKey);
    }
}
