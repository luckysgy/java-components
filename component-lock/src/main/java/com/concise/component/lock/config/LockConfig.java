package com.concise.component.lock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * 锁的配置
 * @author shenguangyang
 * @date 2021/7/10 15:00
 */
@Configuration
public class LockConfig {

    /**
     * 分布式锁
     * @param redisConnectionFactory 连接工厂
     * @return
     */
    @Bean(destroyMethod = "destroy")
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "lock");
    }
}
