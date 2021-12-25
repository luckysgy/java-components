package com.concise.component.storage.common.registerbucket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储桶名
 * @author shenguangyang
 * @date 2021-09-30 21:12
 */
public interface StorageBucketName {

//    /**
//     * 获取全部桶名
//     * @return 全部桶名集合
//     */
//    public static List<String> getAllBucketName() {
//        List<String> bucketNames = new ArrayList<>();
//        // 指定扫描的包名
//        Reflections reflections = new Reflections("com.simplifydev");
//        //component是个接口，获取在指定包扫描的目录所有的实现类
//        Set<Class<? extends StorageBucketName>> classes = reflections.getSubTypesOf(StorageBucketName.class);
//        for (Class<? extends StorageBucketName> aClass : classes) {
//            //遍历执行
//            try {
//                StorageBucketName storageBucketName = aClass.newInstance();
//                subClassMap.put(storageBucketName.getClass().getName(), storageBucketName);
//                bucketNames.add(storageBucketName.getBucketName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return bucketNames;
//    }

    /**
     * 获取桶名, 可以做一些是否合法校验操作
     *
     *         String bucketName = bucketName();
     *         if (StringUtils.isEmpty(bucketName)) {
     *             throw new RuntimeException("桶名不能为空, class: " + this.getClass().getName());
     *         }
     *         return bucketName;
     */
    String getBucketName();
}
