package com.concise.component.cache.redis.service;

import com.concise.component.cache.common.service.ValueOps;
import com.concise.component.cache.redis.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author shenguangyang
 * @date 2021-10-02 上午8:12
 */
@Component
public class RedisValueOps implements ValueOps {
    @Override
    public <T> void set(String key, T value) {
        RedisUtils.StringOps.set(key, value);
    }

    @Override
    public <T> void setEx(String key, T value, long timeout, TimeUnit unit) {
        RedisUtils.StringOps.setEx(key, value, timeout, unit);
    }

    @Override
    public <T> void setEx(String key, T value, long timeout) {
        RedisUtils.StringOps.setEx(key, value, timeout);
    }

    @Override
    public <T> T get(String key) {
        return (T) RedisUtils.StringOps.get(key);
    }

    @Override
    public <T> T getAndSet(String key, T newValue) {
        return (T) RedisUtils.StringOps.getAndSet(key, newValue);
    }

    @Override
    public long incrementBy(String key, long increment) {
        return RedisUtils.StringOps.incrBy(key, increment);
    }

    @Override
    public double incrementByFloat(String key, double increment) {
        return RedisUtils.StringOps.incrByFloat(key, increment);
    }
}
