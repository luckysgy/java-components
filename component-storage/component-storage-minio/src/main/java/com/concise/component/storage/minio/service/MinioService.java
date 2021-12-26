package com.concise.component.storage.minio.service;

import cn.hutool.core.util.RandomUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.storagetype.ConditionalOnStorageType;
import com.concise.component.storage.common.autoconfig.StorageProperties;
import com.concise.component.storage.common.registerbucket.StorageBucketNameHandler;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import com.concise.component.storage.common.url.UrlTypesEnum;
import com.concise.component.storage.common.registerbucket.StorageBucketName;
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
@ConditionalOnStorageType(type = StorageTypesEnum.MINIO)
public class MinioService extends StorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    public MinioService(StorageProperties storageProperties) {
        super(storageProperties);
    }

    @Override
    public <T extends StorageBucketName> void uploadText(Class<T> bucketNameClass, String text, String objectName) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        MinioUtils.uploadFile(StorageBucketNameHandler.getBucketName(bucketNameClass), inputStream, "text/plain", objectName);
    }

    @Override
    public <T extends StorageBucketName> void uploadFile(Class<T> bucketNameClass, InputStream inputStream, String contentType, String objectName) throws Exception {
        MinioUtils.uploadFile(StorageBucketNameHandler.getBucketName(bucketNameClass), inputStream, contentType, objectName);
    }

    @Override
    public <T extends StorageBucketName> String getFilePermanentUrl(Class<T> bucketNameClass, String objectName, UrlTypesEnum urlTypes) {
        String bucketName = StorageBucketNameHandler.getBucketName(bucketNameClass);
        if (UrlTypesEnum.LAN.equals(urlTypes)) {
            String lan = storageProperties.getUrl().getLan();
            return StringUtils.join(lan, "/", bucketName, "/", objectName);
        } else {
            String wan = storageProperties.getUrl().getWan();
            return StringUtils.join(wan, "/", bucketName, "/", objectName);
        }
    }

    @Override
    public <T extends StorageBucketName> InputStream getFile(Class<T> bucketNameClass, String objectName) {
        return MinioUtils.getFile(StorageBucketNameHandler.getBucketName(bucketNameClass), objectName);
    }

    @Override
    public <T extends StorageBucketName> Boolean createBucket(Class<T> bucketNameClass, Boolean randomSuffix) {
        String bucketName = StorageBucketNameHandler.getBucketName(bucketNameClass);
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
