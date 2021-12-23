package com.concise.component.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021/7/25 17:46
 */
@ComponentScan(basePackages = "com.concise.component.feign")
public class FeignMainConfig {
    private static final Logger log = LoggerFactory.getLogger(FeignMainConfig.class);
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.feign");
    }
}
