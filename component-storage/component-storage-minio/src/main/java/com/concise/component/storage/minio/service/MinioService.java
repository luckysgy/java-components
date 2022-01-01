package com.concise.component.storage.minio.service;

import cn.hutool.core.util.RandomUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.storagetype.ConditionalOnStorageType;
import com.concise.component.storage.common.autoconfig.StorageProperties;
import com.concise.component.storage.common.registerbucket.StorageBucketManageHandler;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import com.concise.component.storage.common.url.UrlTypesEnum;
import com.concise.component.storage.common.registerbucket.StorageBucketManage;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.minio.utils.MinioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    public <T extends StorageBucketManage> void uploadText(Class<T> storageBucket, String text, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        MinioUtils.uploadFile(bucketName, inputStream, "text/plain", objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> void uploadFile(Class<T> storageBucket, InputStream inputStream, String contentType, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        MinioUtils.uploadFile(bucketName, inputStream, contentType, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> String getFilePermanentUrl(Class<T> storageBucket, String objectName, UrlTypesEnum urlTypes) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        if (UrlTypesEnum.LAN.equals(urlTypes)) {
            String lan = storageProperties.getUrl().getLan();
            return StringUtils.join(lan, "/", bucketName, "/", objectNamePre + objectName);
        } else {
            String wan = storageProperties.getUrl().getWan();
            return StringUtils.join(wan, "/", bucketName, "/", objectNamePre + objectName);
        }
    }

    @Override
    public <T extends StorageBucketManage> InputStream getFile(Class<T> storageBucket, String objectName) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        return MinioUtils.getFile(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> Boolean createBucket(Class<T> storageBucket, Boolean randomSuffix) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        try {
            if (randomSuffix != null && randomSuffix) {
                MinioUtils.createBucket( bucketName + "-" + RandomUtil.randomString(8));
            } else {
                MinioUtils.createBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("createBucket::bucketName = [{}] message = {}",objectNamePre, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public <T extends StorageBucketManage> void deleteObjects(Class<T> storageBucket, List<String> objectNameList) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        List<String> objectNames = new ArrayList<>(objectNameList.size());
        for (String objectName : objectNameList) {
            objectNames.add(objectNamePre + objectName);
        }
        MinioUtils.deleteObjects(bucketName, objectNames);
    }

    @Override
    public <T extends StorageBucketManage> void deleteObject(Class<T> storageBucket, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        MinioUtils.deleteObject(bucketName, objectNamePre + objectName);
    }
}
