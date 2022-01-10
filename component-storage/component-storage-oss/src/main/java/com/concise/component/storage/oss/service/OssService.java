package com.concise.component.storage.oss.service;

import cn.hutool.core.util.RandomUtil;
import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.StorageProperties;
import com.concise.component.storage.common.registerbucket.StorageBucketManage;
import com.concise.component.storage.common.registerbucket.StorageBucketManageHandler;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.common.storagetype.ConditionalOnStorageType;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import com.concise.component.storage.common.url.UrlTypesEnum;
import com.concise.component.storage.oss.utils.OssUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2021/7/17 13:41
 */
@Component
@ConditionalOnStorageType(type = StorageTypesEnum.OSS)
public class OssService extends StorageService {
    private static final Logger log = LoggerFactory.getLogger(OssService.class);
    /**
     * 批量删除组大小
     */
    private static final Integer DELETE_BATCH_GROUP_SIZE = 900;

    public OssService(StorageProperties storageProperties) {
        super(storageProperties);
    }

    @Override
    public <T extends StorageBucketManage> void uploadText(Class<T> storageBucket, String text, String objectName) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        OssUtils.upload(inputStream, bucketName, objectNamePre + objectName, "text/plain");
    }

    @Override
    public <T extends StorageBucketManage> void uploadFile(Class<T> storageBucket, InputStream inputStream, String contentType, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        OssUtils.upload(inputStream, bucketName, objectNamePre + objectName, contentType);
    }

    @Override
    public <T extends StorageBucketManage> String getFilePermanentUrl(Class<T> storageBucket, String objectName, UrlTypesEnum urlTypesEnum) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        StorageProperties.Oss oss = storageProperties.getOss();
        Boolean enableProxy = oss.getProxy().getEnable();
        // 使能oss代理
        if (enableProxy) {
            if (UrlTypesEnum.WAN.equals(urlTypesEnum)) {
                String wan = storageProperties.getUrl().getWan();
                return StringUtils.join(wan, "/", bucketName,"/",objectNamePre + objectName);
            } else {
                String lan = storageProperties.getUrl().getLan();
                return StringUtils.join(lan, "/", bucketName, "/", objectNamePre + objectName);
            }
        } else {
            String endpoint = oss.getEndpoint();
            endpoint = endpoint.replace("https://", "")
                    .replace("http://", "");
            return StringUtils.join("https://", bucketName, ".", endpoint, "/", objectNamePre + objectName);
        }
    }

    @Override
    public <T extends StorageBucketManage> InputStream getFile(Class<T> storageBucket, String objectName) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        return OssUtils.getFile(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> Boolean createBucket(Class<T> storageBucket, Boolean randomSuffix) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        try {
            if (randomSuffix != null && randomSuffix) {
                return OssUtils.createBucket(bucketName + "-" + RandomUtil.randomString(8));
            }
            return OssUtils.createBucket(bucketName);
        } catch (Exception e) {
            log.error("createBucket::bucketName = [{}] message = {}",bucketName, e.getMessage());
            return false;
        }
    }

    /**
     * 一次最大删除900左右文件
     * @param storageBucket 桶
     * @param objectNameList 对象名集合
     * @param <T>
     * @throws Exception
     */
    @Override
    public <T extends StorageBucketManage> void deleteObjects(Class<T> storageBucket, List<String> objectNameList) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        List<String> objectNames = new ArrayList<>(objectNameList.size());
        for (String objectName : objectNameList) {
            objectNames.add(objectNamePre + objectName);
        }
        for (List<String> groupObjectNames : Lists.partition(objectNames, DELETE_BATCH_GROUP_SIZE)) {
            int deleteSize = OssUtils.deleteObjects(bucketName, groupObjectNames);
            if (deleteSize != groupObjectNames.size()) {
                throw new BizException("实际删除的文件数量: " + deleteSize + ", 目标删除文件数量: " + groupObjectNames.size());
            }
        }
    }

    @Override
    public <T extends StorageBucketManage> void deleteObject(Class<T> storageBucket, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        OssUtils.deleteObject(bucketName, objectNamePre + objectName);
    }
}
