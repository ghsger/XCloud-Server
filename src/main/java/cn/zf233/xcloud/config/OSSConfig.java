package cn.zf233.xcloud.config;

import cn.zf233.xcloud.util.OSSUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zf233 on 2021/1/12
 */
@Configuration
public class OSSConfig {

    @Value("${alibaba.oss.endpoint}")
    private String endpoint;
    @Value("${alibaba.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${alibaba.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${alibaba.oss.bucketName}")
    private String bucketName;

    @Bean(destroyMethod = "destroy")
    public OSSUtil ossUtil() {
        OSSUtil ossUtil = new OSSUtil();
        ossUtil.setEndpoint(endpoint);
        ossUtil.setAccessKeyId(accessKeyId);
        ossUtil.setAccessKeySecret(accessKeySecret);
        ossUtil.setBucketName(bucketName);
        return ossUtil;
    }
}
