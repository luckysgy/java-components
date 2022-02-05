package com.concise.component.storage.common.registerstoragemanage;

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
public class StorageManageHandler {
    private static StorageProperties storageProperties;

    public StorageManageHandler(StorageProperties storageProperties) {
        StorageManageHandler.storageProperties = storageProperties;
    }

    /**
     * key 子类全路径, value 桶的类
     */
    private static final Map<String, StorageManage> storageBucketNameSub = new ConcurrentHashMap<>();

    public static <T extends StorageManage> String getObjectNamePre(Class<T> storageBucketClass) {
        if (storageBucketClass == null) {
            throw new BizException("storageBucketClass == null");
        }
        StorageManage storageManage = storageBucketNameSub.get(storageBucketClass.getName());
        if (storageManage == null) {
            throw new BizException("桶名不存在, storageBucketClass: " + storageBucketClass.getName());
        }
        return storageManage.getObjectNamePre();
    }

    public static <T extends StorageManage> String getBucketName(Class<T> storageBucketClass) {
        if (storageBucketClass == null) {
            throw new BizException("storageBucketClass == null");
        }
        return getBucketName();
    }

    private static String getBucketName() {
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
        bucketNames.add(getBucketName());
        return bucketNames;
    }

    public static void addStorageBucketSub(StorageManage storageManage) {
        String objectNamePre = storageManage.getObjectNamePre();
        if (objectNamePre == null) {
            throw new BizException(storageManage.getClass().getName() + "#getObjectNamePre() 不能为 null");
        }
        if (StringUtils.isNotEmpty(objectNamePre) && !objectNamePre.endsWith("/")) {
            throw new BizException(storageManage.getClass().getName() + "#getObjectNamePre() 必须以 / 结尾");
        }
        storageBucketNameSub.put(storageManage.getClass().getName(), storageManage);
    }
}
