package com.concise.component.storage.minio.service;

import cn.hutool.core.util.RandomUtil;

import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.annotation.ConditionalOnStorageType;
import com.concise.component.storage.common.config.StorageProperties;
import com.concise.component.storage.common.enums.StorageTypes;
import com.concise.component.storage.common.enums.UrlTypes;
import com.concise.component.storage.common.expand.StorageBucketName;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.minio.utils.MinioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 为minio 设置 nginx 代理: http://docs.minio.org.cn/docs/master/setup-nginx-proxy-with-minio
 * @author shenguangyang
 * @date 2021/7/17 13:41
 */
@Component
@ConditionalOnStorageType(type = StorageTypes.MINIO)
public class MinioService extends StorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    public MinioService(StorageProperties storageProperties) {
        super(storageProperties);
    }

    @Override
    public <T extends StorageBucketName> void uploadText(Class<T> bucketNameClass, String text, String objectName) throws Exception {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        MinioUtils.uploadFile(storageBucketName.getBucketName(), inputStream, "text/plain", objectName);
    }

    @Override
    public <T extends StorageBucketName> void uploadFile(Class<T> bucketNameClass, InputStream inputStream, String contentType, String objectName) throws Exception {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        MinioUtils.uploadFile(storageBucketName.getBucketName(), inputStream, contentType, objectName);
    }

    @Override
    public <T extends StorageBucketName> String getFilePermanentUrl(Class<T> bucketNameClass, String objectName, UrlTypes urlTypes) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        String bucketName = storageBucketName.getBucketName();
        if (UrlTypes.LAN.equals(urlTypes)) {
            String lan = storageProperties.getUrl().getLan();
            return StringUtils.join(lan, "/", bucketName, "/", objectName);
        } else {
            String wan = storageProperties.getUrl().getWan();
            return StringUtils.join(wan, "/", bucketName, "/", objectName);
        }
    }

    @Override
    public <T extends StorageBucketName> InputStream getFile(Class<T> bucketNameClass, String objectName) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        return MinioUtils.getFile(storageBucketName.getBucketName(), objectName);
    }

    @Override
    public <T extends StorageBucketName> Boolean createBucket(Class<T> bucketNameClass, Boolean randomSuffix) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        String bucketName = storageBucketName.getBucketName();
        try {
            if (randomSuffix != null && randomSuffix) {
                MinioUtils.createBucket( bucketName + "-" + RandomUtil.randomString(8));
            } else {
                MinioUtils.createBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("createBucket::bucketName = [{}] message = {}",bucketName, e.getMessage());
            return false;
        }
        return true;
    }
}
