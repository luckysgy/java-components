package com.concise.component.idgenerator;

import com.concise.component.idgenerator.config.IdGeneratorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author shenguangyang
 * @date 2021-12-21 22:21
 */
@Configuration
@EnableConfigurationProperties(IdGeneratorProperties.class)
@ComponentScan(basePackages = "com.concise.component.idgenerator")
public class ComponentIdGeneratorAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentIdGeneratorAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.idgenerator");
    }
}
