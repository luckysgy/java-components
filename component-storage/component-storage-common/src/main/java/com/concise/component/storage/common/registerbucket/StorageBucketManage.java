package com.concise.component.storage.common.registerbucket;

/**
 * 存储桶名
 * @author shenguangyang
 * @date 2021-09-30 21:12
 */
public interface StorageBucketManage {
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

    /**
     * 获取对象名前缀
     * @return
     */
    String getObjectNamePre();
}
