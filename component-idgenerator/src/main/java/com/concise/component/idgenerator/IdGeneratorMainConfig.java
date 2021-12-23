package com.concise.component.idgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-21 22:21
 */
@ComponentScan(basePackages = "com.concise.component.idgenerator")
public class IdGeneratorMainConfig {
    private static final Logger log = LoggerFactory.getLogger(IdGeneratorMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.idgenerator");
    }
}
