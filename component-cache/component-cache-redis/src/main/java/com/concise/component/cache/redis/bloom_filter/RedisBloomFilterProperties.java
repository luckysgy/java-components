package com.concise.component.cache.redis.bloom_filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenguangyang
 * @date 2022-03-31 7:14
 */
@Data
@Configuration
@ConfigurationProperties("bloom.filter")
public class RedisBloomFilterProperties {
    /**
     * 预计数据总量
     */
    private long expectedInsertions;
    /**
     * 容错率
     */
    private double fpp;
}
