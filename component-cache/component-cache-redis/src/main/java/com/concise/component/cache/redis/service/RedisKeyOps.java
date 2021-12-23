package com.concise.component.cache.redis.service;

import com.concise.component.cache.common.service.KeyOps;
import com.concise.component.cache.redis.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author shenguangyang
 * @date 2021-10-02 上午8:10
 */
@Component
public class RedisKeyOps implements KeyOps {
    @Override
    public boolean delete(String key) {
        return RedisUtils.KeyOps.delete(key);
    }

    @Override
    public long delete(Collection<String> keys) {
        return RedisUtils.KeyOps.delete(keys);
    }

    @Override
    public byte[] dump(String key) {
        return RedisUtils.KeyOps.dump(key);
    }

    @Override
    public void restore(String key, byte[] value, long timeToLive, TimeUnit unit) {
        RedisUtils.KeyOps.restore(key, value, timeToLive, unit);
    }

    @Override
    public boolean hasKey(String key) {
        return RedisUtils.KeyOps.hasKey(key);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return RedisUtils.KeyOps.expire(key, timeout, unit);
    }

    @Override
    public Set<String> keys(String pattern) {
        return RedisUtils.KeyOps.keys(pattern);
    }
}
