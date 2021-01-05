package cn.zf233.xcloud.config;

import cn.zf233.xcloud.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by zf233 on 2021/1/2
 */
@Configuration
public class RedisUtilConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

    @Bean(value = "filePermissionRedisUtil")
    public RedisUtil filePermissionRedisUtil() {
        return new RedisUtil();
    }

}
