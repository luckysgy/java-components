package com.concise.component.core.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池配置
 * @author shenguangyang
 * @date 2022-03-29 19:30
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {
    /** 核心线程数 */
    private Integer corePoolSize = 2;
    /** 最大线程数 */
    private Integer maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    /** 队列数 */
    private Integer queueCapacity = 100;
    /* 当线程空闲时间达到keepAliveTime, 该线程会退出 */
    private Integer keepAliveSeconds = 60;
}
