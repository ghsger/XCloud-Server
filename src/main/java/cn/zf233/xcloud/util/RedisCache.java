package cn.zf233.xcloud.util;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zf233 on 2021/1/22
 */
public class RedisCache implements Cache {

    private final String id;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private RedisTemplate<Object, Object> redisTemplate;

//    private static final long EXPIRE_TIME_IN_MINUTES = 30;

    public RedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    private RedisTemplate<Object, Object> getRedisTemplate(){
        if (this.redisTemplate == null) {
            this.redisTemplate = SpringUtil.getBean("redisTemplateOfMybatisCache");
        }
        return this.redisTemplate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
        redisTemplate.boundHashOps(getId()).put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
        return redisTemplate.boundHashOps(getId()).get(key);
    }

    @Override
    public Object removeObject(Object key) {
        RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
        return redisTemplate.boundHashOps(getId()).delete(key);
    }

    @Override
    public void clear() {
        RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
        redisTemplate.delete(getId());
    }

    @Override
    public int getSize() {
        RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
        Long size = redisTemplate.boundHashOps(getId()).size();
        return size == null ? 0 : size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }
}
