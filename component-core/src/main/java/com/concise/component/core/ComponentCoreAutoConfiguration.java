package com.concise.component.core;

import com.concise.component.core.idgenerator.IdGeneratorProperties;
import com.concise.component.core.thread.ThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-23 6:17
 */
@Import(IniEnvRegistrar.class)
@EnableConfigurationProperties({ThreadPoolProperties.class, IdGeneratorProperties.class})
@ComponentScan(basePackages = "com.concise.component.core",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = DataSourceAutoConfiguration.class)
})
public class ComponentCoreAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentCoreAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.core");
    }
}