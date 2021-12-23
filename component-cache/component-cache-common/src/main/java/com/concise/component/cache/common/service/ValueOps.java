package com.concise.component.cache.common.service;

import java.util.concurrent.TimeUnit;

/**
 * 操作缓存 -- string
 * 注释中大部分都是以redis进行举例子
 * @author shenguangyang
 * @date 2021-10-02 上午6:25
 */
public interface ValueOps {
    /**
     * 设置key-value
     *
     * 注: 若已存在相同的key, 那么原来的key-value会被丢弃。
     *
     * @param key key
     * @param value key对应的value
     */
    <T> void set(String key, T value);

    /**
     * 设置key-value
     *
     * 注: 若已存在相同的key, 那么原来的key-value会被丢弃
     *
     * @param key key
     * @param value key对应的value
     * @param timeout 过时时长
     * @param unit timeout的单位
     */
    <T> void setEx(String key, T value, long timeout, TimeUnit unit);

    /**
     * 设置key-value
     * @param key key
     * @param value key对应的value
     * @param timeout 过时的时长 ，单位是s
     */
    <T> void setEx(String key, T value, long timeout);

    /**
     * 根据key，获取到对应的value值
     *
     * @param key key-value对应的key
     * @return  该key对应的值。
     *          注: 若key不存在， 则返回null。
     */
    <T> T get(String key);

    /**
     * 给指定key设置新的value, 并返回旧的value
     *
     * 注: 若redis中不存在key, 那么此操作仍然可以成功， 不过返回的旧值是null
     *
     * @param key 定位value的key
     * @param newValue 要为该key设置的新的value值
     * @return  旧的value值
     */
    <T> T getAndSet(String key, T newValue);

    /**
     * 增/减 整数
     *
     * 注: 负数则为减。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     *     抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key 用于定位value的key
     * @param increment 增加多少
     * @return  增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    long incrementBy(String key, long increment);

    /**
     * 增/减 浮点数
     *
     * 注: 慎用浮点数，会有精度问题。
     *     如: 先 RedisUtil.StringOps.set("ds", "123");
     *         然后再RedisUtil.StringOps.incrByFloat("ds", 100.6);
     *         就会看到精度问题。
     * 注: 负数则为减。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     *     抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key 用于定位value的key
     * @param increment 增加多少
     * @return  增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    double incrementByFloat(String key, double increment);
}
