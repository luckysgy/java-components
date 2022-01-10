package com.concise.component.storage.common.registerbucket;

import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.StorageProperties;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenguangyang
 * @date 2021-12-25 21:17
 */
@Component
public class StorageBucketManageHandler {
    private static StorageProperties storageProperties;

    public StorageBucketManageHandler(StorageProperties storageProperties) {
        StorageBucketManageHandler.storageProperties = storageProperties;
    }

    /**
     * key 子类全路径, value 桶的类
     */
    private static final Map<String, StorageBucketManage> storageBucketNameSub = new ConcurrentHashMap<>();

    public static <T extends StorageBucketManage> String getObjectNamePre(Class<T> storageBucketClass) {
        if (storageBucketClass == null) {
            throw new BizException("storageBucketClass == null");
        }
        StorageBucketManage storageBucketManage = storageBucketNameSub.get(storageBucketClass.getName());
        if (storageBucketManage == null) {
            throw new BizException("桶名不存在, storageBucketClass: " + storageBucketClass.getName());
        }
        return storageBucketManage.getObjectNamePre();
    }

    public static <T extends StorageBucketManage> String getBucketName(Class<T> storageBucketClass) {
        if (storageBucketClass == null) {
            throw new BizException("storageBucketClass == null");
        }
        if (storageProperties.getIsOneBucket()) {
            return getOneBucketName();
        }
        StorageBucketManage storageBucketManage = storageBucketNameSub.get(storageBucketClass.getName());
        if (storageBucketManage == null) {
            throw new BizException("桶名不存在, storageBucketClass: " + storageBucketClass.getName());
        }
        return storageBucketManage.getBucketName();
    }

    private static String getOneBucketName() {
        String type = storageProperties.getType();
        if (StorageTypesEnum.MINIO.getType().equals(type)) {
            return storageProperties.getMinio().getBucketName();
        } else if (StorageTypesEnum.OSS.getType().equals(type)) {
            return storageProperties.getOss().getBucketName();
        } else {
            throw new BizException("存储类型不存在");
        }
    }
    public static List<String> getAllBucketName() {
        List<String> bucketNames = new ArrayList<>();
        if (storageProperties.getIsOneBucket()) {
            bucketNames.add(getOneBucketName());
        } else {
            for (Map.Entry<String, StorageBucketManage> entry : storageBucketNameSub.entrySet()) {
                bucketNames.add(entry.getValue().getBucketName());
            }
        }
        return bucketNames;
    }

    public static void addStorageBucketSub(StorageBucketManage storageBucketManage) {
        String objectNamePre = storageBucketManage.getObjectNamePre();
        if (objectNamePre == null) {
            throw new BizException(storageBucketManage.getClass().getName() + "#getObjectNamePre() 不能为 null");
        }
        if (StringUtils.isNotEmpty(objectNamePre) && !objectNamePre.endsWith("/")) {
            throw new BizException(storageBucketManage.getClass().getName() + "#getObjectNamePre() 必须以 / 结尾");
        }
        storageBucketNameSub.put(storageBucketManage.getClass().getName(), storageBucketManage);
    }
}
