package com.concise.component.cache.redis;

import com.concise.component.cache.redis.bloom_filter.RedisBloomFilterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;

import javax.annotation.PostConstruct;

/**
 * 过期回调函数, 需要继承 {@link KeyExpirationEventMessageListener}
 * 实现的方法如下:
 * <code>
 *     public void onMessage(Message message, byte[] pattern) {
 *         System.out.println(new String(message.getBody()));
 *         System.out.println(new String(message.getChannel()));
 *         System.out.println(new String(pattern));
 *         super.onMessage(message, pattern);
 *     }
 * </code>
 *
 *
 * @author shenguangyang
 * @date 2021-10-02 上午6:12
 */
@EnableConfigurationProperties({RedisBloomFilterProperties.class})
@ComponentScan(basePackages = "com.concise.component.cache.redis")
public class ComponentRedisAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ComponentRedisAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("init com.concise.component.cache.redis");
    }
}
