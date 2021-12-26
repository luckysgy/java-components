package com.concise.component.storage.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import com.concise.component.storage.common.autoconfig.StorageProperties;
import com.concise.component.storage.common.registerbucket.StorageBucketNameHandler;
import com.concise.component.storage.oss.utils.OssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Description:
 *
 * @author shenguangyang
 * @date 2021/03/25
 */
@Slf4j
@Configuration
public class OssConfig {
    @Autowired
    StorageProperties storageProperties;

    @PostConstruct
    public void init() {
        Boolean enable = storageProperties.getEnable();
        StorageProperties.Oss oss = storageProperties.getOss();
        Boolean storageEnable = oss.getEnable();
        if (!enable) {
            return;
        }
        if (!storageEnable) {
            return;
        }
        StorageProperties.Url url = storageProperties.getUrl();

        log.info("开始初始化oss");
        OSS ossClient = new OSSClientBuilder().build(
                oss.getEndpoint(),oss.getAccessKeyId(),oss.getSecretAccessKey());
        List<String> allBucketName = StorageBucketNameHandler.getAllBucketName();
        for (String bucketName : allBucketName) {
            boolean exists = ossClient.doesBucketExist(bucketName);
            //判定是否存在此存储空间
            if (!exists) {
                // 创建存储空间。
                log.info("oss创建bucket: {}", bucketName);
                ossClient.createBucket(bucketName);
                continue;
            }
            log.info("oss存在bucket: {}", bucketName);
        }

        OssUtils.init(ossClient, oss, url);
        log.info("oss初始化完成");
    }
}
