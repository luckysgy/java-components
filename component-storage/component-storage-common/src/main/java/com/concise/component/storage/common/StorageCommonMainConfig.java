package com.concise.component.storage.common;

import com.concise.component.storage.common.registerbucket.StorageBucketManageSubScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:12
 */
@StorageBucketManageSubScan
@ComponentScan(basePackages = "com.concise.component.storage.common")
public class StorageCommonMainConfig {
    private static final Logger log = LoggerFactory.getLogger(StorageCommonMainConfig.class);
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.storage.common");
    }
}
