package cn.zf233.xcloud.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisTemplateConfig {

    // write
    @Value("${spring.redis.write.host}")
    private String writeHost;
    @Value("${spring.redis.write.port}")
    private Integer writePort;
    @Value("${spring.redis.write.password}")
    private String writePassword;
    @Value("${spring.redis.write.database}")
    private Integer writeDatabase;

    // read
    @Value("${spring.redis.read.host}")
    private String readHost;
    @Value("${spring.redis.read.port}")
    private Integer readPort;
    @Value("${spring.redis.read.password}")
    private String readPassword;
    @Value("${spring.redis.read.database}")
    private Integer readDataBase;

    private static final int MAX_IDLE = 128; //最大空闲连接数
    private static final int MAX_TOTAL = 512; //最大连接数
    private static final long MAX_WAIT_MILLIS = 10000; //建立连接最长等待时间


    //配置工厂
    public RedisConnectionFactory connectionFactory(String host, int port, String password, int maxIdle,
                                                    int maxTotal, long maxWaitMillis, int index) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);

        if (!StringUtils.isEmpty(password)) {
            jedisConnectionFactory.setPassword(password);
        }

        if (index != 0) {
            jedisConnectionFactory.setDatabase(index);
        }

        jedisConnectionFactory.setPoolConfig(poolConfig(maxIdle, maxTotal, maxWaitMillis, false));
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    //连接池配置
    public JedisPoolConfig poolConfig(int maxIdle, int maxTotal, long maxWaitMillis, boolean testOnBorrow) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        return poolConfig;
    }


    @Bean(name = "writeRedis")
    public RedisTemplate<String, byte[]> writeRedis() {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(
                connectionFactory(writeHost, writePort, writePassword, MAX_IDLE, MAX_TOTAL, MAX_WAIT_MILLIS, writeDatabase));
        return template;
    }

    @Bean(name = "readRedis")
    public RedisTemplate<String, byte[]> readRedis() {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(
                connectionFactory(readHost, readPort, readPassword, MAX_IDLE, MAX_TOTAL, MAX_WAIT_MILLIS, readDataBase));
        return template;
    }
}