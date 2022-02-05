package com.concise.component.storage.common;

import com.concise.component.storage.common.registerstoragemanage.StorageManageSubScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:12
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@StorageManageSubScan
@ComponentScan(basePackages = "com.concise.component.storage.common")
public class ComponentStorageCommonAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentStorageCommonAutoConfiguration.class);
    
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.storage.common");
    }
}
