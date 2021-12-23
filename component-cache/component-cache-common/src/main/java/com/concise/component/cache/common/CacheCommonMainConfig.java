package com.concise.component.cache.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-10-02 上午6:04
 */
@ComponentScan(basePackages = "com.concise.component.cache.common")
public class CacheCommonMainConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheCommonMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.cache.common");
    }
}
