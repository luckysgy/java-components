package com.concise.component.storage.common.expand;

/**
 * @author shenguangyang
 * @date 2021-09-30 21:17
 */
public class DefaultBucketName extends StorageBucketName {
    @Override
    protected String bucketName() {
        return "default";
    }
}
