package com.concise.component.storage.common.registerbucket;

import com.concise.component.core.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author shenguangyang
 * @date 2021-09-30 21:17
 */
@Component
public class DefaultBucketName implements StorageBucketName {
    @Override
    public String getBucketName() {
        return "default";
    }
}
