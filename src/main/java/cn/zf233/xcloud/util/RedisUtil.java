package cn.zf233.xcloud.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zf233 on 2020/11/27
 */
@Component
public class RedisUtil implements DisposableBean {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, byte[]> redisTemplateOfFileCache;

    // String K/V operation
    public void set(String key, String value) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value, 180, TimeUnit.SECONDS);
    }

    public String get(String key) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        return ops.get(key);
    }

    public Long getTimeOutOfKey(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    // String K byte V operation
    public void setFileCache(String key, byte[] value) {
        ValueOperations<String, byte[]> ops = redisTemplateOfFileCache.opsForValue();
        ops.set(key, value);
    }

    public byte[] getFileCache(String key) {
        ValueOperations<String, byte[]> ops = redisTemplateOfFileCache.opsForValue();

        return ops.get(key);
    }

    public Boolean cacheKeyExists(String key) {
        return redisTemplateOfFileCache.hasKey(key);
    }

    public void removeFileCache(String key) {
        redisTemplateOfFileCache.delete(key);
    }

    @Override
    public void destroy() {
        Set<String> keys = stringRedisTemplate.keys("*");

        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }
    }
}