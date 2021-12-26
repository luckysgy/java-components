package com.concise.component.storage.common.registerbucket;

import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenguangyang
 * @date 2021-12-25 21:17
 */
public class StorageBucketNameHandler {
    /**
     * key 子类全路径, value 桶的类
     */
    private static final Map<String, StorageBucketName> storageBucketNameSub = new ConcurrentHashMap<>();

    public static <T extends StorageBucketName> String getBucketName(Class<T> storageBucketClass) {
        if (storageBucketClass == null) {
            throw new BizException("storageBucketClass == null");
        }
        StorageBucketName storageBucketName = storageBucketNameSub.get(storageBucketClass.getName());
        if (storageBucketName == null) {
            throw new BizException("桶名不存在, storageBucketClass: " + storageBucketClass.getName());
        }
        return storageBucketName.getBucketName();
    }

    public static List<String> getAllBucketName() {
        List<String> bucketNames = new ArrayList<>();
        for (Map.Entry<String, StorageBucketName> entry : storageBucketNameSub.entrySet()) {
            bucketNames.add(entry.getValue().getBucketName());
        }
        return bucketNames;
    }

    public static void addStorageBucketSub(StorageBucketName storageBucketName) {
        storageBucketNameSub.put(storageBucketName.getClass().getName(), storageBucketName);
    }
}
