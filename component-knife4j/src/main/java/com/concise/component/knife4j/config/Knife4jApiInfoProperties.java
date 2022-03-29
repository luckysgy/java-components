package com.concise.component.knife4j.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenguangyang
 * @date 2022-03-29 19:51
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "knife4j.info")
public class Knife4jApiInfoProperties {
    private String title;
    private String description;
    private Contact contact;
    private String version;

    @Data
    public static class Contact {
        private String name;
        private String url;
        private String email;
    }
}
