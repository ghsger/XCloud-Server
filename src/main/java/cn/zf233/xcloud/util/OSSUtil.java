package cn.zf233.xcloud.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by zf233 on 2021/1/12
 */
public class OSSUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    volatile private static OSS ossClient;

    public static synchronized OSS getOSSClientSingleton() {
        if (ossClient == null) {
            synchronized (OSSUtil.class) {
                if (ossClient == null) {
                    ossClient = new OSSClientBuilder().build("endpoint", "accessKeyId", "accessKeySecret");
                }

            }
        }
        return ossClient;
    }

    public OSS getOSSClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public void upload(OSS ossClient, String randomName, String oldName, byte[] content) {

        String encodeOldName;

        try {

            // oldName encode
            encodeOldName = URLEncoder.encode(oldName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            // oldName encode fail
            encodeOldName = randomName;
        }

        // Encoding failed to persist with a random name
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, randomName, new ByteArrayInputStream(content));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentDisposition("attachment;filename=" + encodeOldName);
        metadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        metadata.setContentLength(content.length);
        putObjectRequest.setMetadata(metadata);

        ossClient.putObject(putObjectRequest);
    }

    public Boolean objectNameExists(OSS ossClient, String objectName) {
        return ossClient.doesObjectExist(bucketName, objectName);
    }

    public void delete(OSS ossClient, String objectName) {
        ossClient.deleteObject(bucketName, objectName);
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
