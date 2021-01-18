package cn.zf233.xcloud.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.ByteArrayInputStream;

/**
 * Created by zf233 on 2021/1/12
 */
public class OSSUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private volatile OSS ossClient;

    /**
     * 单例
     *
     * @return OSS工具类实例
     */
    private OSS getOSSClient() {
        if (ossClient == null) {
            synchronized (OSSUtil.class) {
                if (ossClient == null) {
                    ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                }
            }
        }
        return ossClient;
    }

    public void upload(String objectName, byte[] content) {
        getOSSClient().putObject(bucketName, objectName, new ByteArrayInputStream(content));
    }

    public Boolean objectNameExists(String objectName) {
        return getOSSClient().doesObjectExist(bucketName, objectName);
    }

    public void delete(String objectName) {
        getOSSClient().deleteObject(bucketName, objectName);
    }

    public void close() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
        ossClient = null;
    }

    public void destroy() {
        close();
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
