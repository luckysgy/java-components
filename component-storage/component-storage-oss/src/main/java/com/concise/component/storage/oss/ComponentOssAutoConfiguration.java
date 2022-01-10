package com.concise.component.storage.oss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 21:25
 */
@ComponentScan(basePackages = "com.concise.component.storage.oss")
public class ComponentOssAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentOssAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.storage.oss");
    }
}
