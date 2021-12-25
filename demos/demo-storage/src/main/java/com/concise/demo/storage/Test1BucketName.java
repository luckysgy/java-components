package com.concise.demo.storage;

import com.concise.component.storage.common.registerbucket.StorageBucketName;
import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021-09-30 21:17
 */
@Component
public class Test1BucketName implements StorageBucketName {
    @Override
    public String getBucketName() {
        return "test1";
    }
}
