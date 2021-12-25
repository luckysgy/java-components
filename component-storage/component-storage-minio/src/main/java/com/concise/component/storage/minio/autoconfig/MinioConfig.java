package com.concise.component.storage.minio.autoconfig;

import com.concise.component.storage.common.autoconfig.StorageProperties;
import com.concise.component.storage.common.registerbucket.StorageBucketHandler;
import com.concise.component.storage.minio.client.CustomMinioClient;
import com.concise.component.storage.minio.utils.MinioUtils;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Description: minio配置类,如果服务器与客户端主机的时间相差太大会启动失败
 *
 * @author shenguangyang
 * @date 2021/03/24
 */
@Configuration
public class MinioConfig {
    /**
     * logger
     */
    private final static Logger log = LoggerFactory.getLogger(MinioConfig.class);

    @Autowired
    private StorageProperties storageProperties;

    @PostConstruct
    public void init() throws Exception{
        Boolean enable = storageProperties.getEnable();
        StorageProperties.Minio minio = storageProperties.getMinio();
        Boolean storageEnable = minio.getEnable();
        if (!enable) {
            return;
        }
        if (!storageEnable) {
            return;
        }
        log.info("Minio文件系统初始化加载");
        StorageProperties.Url url = storageProperties.getUrl();

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minio.getEndpoint())
                        .credentials(minio.getAccessKey(), minio.getSecretKey())
                        .build();
        // 判断Bucket是否存在
        List<String> allBucketName = StorageBucketHandler.getAllBucketName();
        for (String bucketName : allBucketName) {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if(isExist) {
                log.info("Minio文件系统Bucket: {} 已存在", bucketName);
            } else {
                // 不存在创建一个新的Bucket
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Minio已创建Bucket: {}", bucketName);
            }
        }

        CustomMinioClient customMinioClient = new CustomMinioClient(minioClient);
        MinioUtils.init(customMinioClient, minio, url);
        log.info("Minio文件系统初始化完成");
    }
}
