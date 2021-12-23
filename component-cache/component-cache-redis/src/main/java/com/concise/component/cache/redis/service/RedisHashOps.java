package com.concise.component.cache.redis.service;

import com.concise.component.cache.common.service.HashOps;
import com.concise.component.cache.redis.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shenguangyang
 * @date 2021-10-02 上午8:12
 */
@Component
public class RedisHashOps implements HashOps {
    @Override
    public <T> void put(String key, String entryKey, T entryValue) {
        RedisUtils.HashOps.hPut(key, entryKey, entryValue);
    }

    @Override
    public <T> boolean putIfAbsent(String key, Object entryKey, T entryValue) {
        return RedisUtils.HashOps.hPutIfAbsent(key, entryKey, entryValue);
    }

    @Override
    public <T> T get(String key, Object entryKey) {
        return (T) RedisUtils.HashOps.hGet(key, entryKey);
    }

    @Override
    public <T> Map<String, T> getAll(String key) {
        return (Map<String, T>) RedisUtils.HashOps.hGetAll(key);
    }

    @Override
    public <T> List<T> multiGet(String key, Collection<Object> entryKeys) {
        return (List<T>) RedisUtils.HashOps.hMultiGet(key, entryKeys);
    }

    @Override
    public long delete(String key, Object... entryKeys) {
        return RedisUtils.HashOps.hDelete(key, entryKeys);
    }

    @Override
    public boolean exists(String key, String entryKey) {
        return RedisUtils.HashOps.hExists(key, entryKey);
    }

    @Override
    public long incrementBy(String key, Object entryKey, long increment) {
        return RedisUtils.HashOps.hIncrBy(key, entryKey, increment);
    }

    @Override
    public double incrementByFloat(String key, Object entryKey, double increment) {
        return RedisUtils.HashOps.hIncrByFloat(key, entryKey, increment);
    }

    @Override
    public Set<Object> keys(String key) {
        return RedisUtils.HashOps.hKeys(key);
    }

    @Override
    public <T> List<T> values(String key) {
        return (List<T>) RedisUtils.HashOps.hValues(key);
    }
}
