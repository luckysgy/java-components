package com.concise.component.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 22:15
 */
@ComponentScan(basePackages = "com.concise.component.lock")
public class ComponentLockAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentLockAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.lock");
    }
}
