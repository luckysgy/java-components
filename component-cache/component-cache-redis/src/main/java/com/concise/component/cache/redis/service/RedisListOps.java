package com.concise.component.cache.redis.service;

import com.concise.component.cache.common.service.ListOps;
import com.concise.component.cache.redis.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author shenguangyang
 * @date 2021-10-02 上午8:12
 */
@Component
public class RedisListOps implements ListOps {
    @Override
    public <T> long leftPush(String key, T item) {
        return RedisUtils.ListOps.lLeftPush(key, item);
    }

    @Override
    public long leftPushAll(String key, Object... items) {
        return RedisUtils.ListOps.lLeftPushAll(key, items);
    }

    @Override
    public <T> long leftPushAll(String key, Collection<T> items) {
        return RedisUtils.ListOps.lLeftPushAll(key, items);
    }

    @Override
    public <T> long leftPushIfPresent(String key, T item) {
        return RedisUtils.ListOps.lLeftPushIfPresent(key, item);
    }

    @Override
    public <T> long leftPush(String key, String pivot, T item) {
        return RedisUtils.ListOps.lLeftPush(key, pivot, item);
    }

    @Override
    public <T> long rightPush(String key, T item) {
        return RedisUtils.ListOps.lRightPush(key, item);
    }

    @Override
    public long rightPushAll(String key, Object... items) {
        return RedisUtils.ListOps.lRightPushAll(key, items);
    }

    @Override
    public <T> long rightPushAll(String key, Collection<T> items) {
        return RedisUtils.ListOps.lRightPushAll(key, items);
    }

    @Override
    public <T> long rightPushIfPresent(String key, T item) {
        return RedisUtils.ListOps.lRightPushIfPresent(key, item);
    }

    @Override
    public <T> long rightPush(String key, String pivot, T item) {
        return RedisUtils.ListOps.lRightPush(key, pivot, item);
    }

    @Override
    public <T> T leftPop(String key) {
        return (T) RedisUtils.ListOps.lLeftPop(key);
    }

    @Override
    public <T> T leftPop(String key, long timeout, TimeUnit unit) {
        return (T) RedisUtils.ListOps.lLeftPop(key, timeout, unit);
    }

    @Override
    public <T> T rightPop(String key) {
        return RedisUtils.ListOps.lRightPop(key);
    }

    @Override
    public <T> T rightPop(String key, long timeout, TimeUnit unit) {
        return (T) RedisUtils.ListOps.lRightPop(key, timeout, unit);
    }

    @Override
    public <T> T rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return (T) RedisUtils.ListOps.lRightPopAndLeftPush(sourceKey, destinationKey);
    }
}
