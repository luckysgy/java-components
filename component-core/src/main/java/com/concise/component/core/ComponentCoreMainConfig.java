package com.concise.component.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 6:17
 */
@ComponentScan(basePackages = "com.concise.component.core",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = DataSourceAutoConfiguration.class)
})
public class ComponentCoreMainConfig {
    private static final Logger log = LoggerFactory.getLogger(ComponentCoreMainConfig.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.core");
    }
}