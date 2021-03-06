package com.concise.component.storage.minio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:17
 */
@ComponentScan(basePackages = "com.concise.component.storage.minio")
public class ComponentMinioAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentMinioAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.storage.minio");
    }
}
