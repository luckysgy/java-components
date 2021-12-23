package com.concise.component.cache.common.service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 操作缓存 -- key
 * 注释中大部分都是以redis进行举例子
 * @author shenguangyang
 * @date 2021-10-02 上午6:20
 */
public interface KeyOps {
    /**
     * 根据key, 删除redis中的对应key-value
     *
     *  注: 若删除失败, 则返回false。

     * @param key
     *            要删除的key
     * @return  删除是否成功
     */
    boolean delete(String key);

    /**
     * 根据keys, 批量删除key-value
     *
     * 注: 若redis中，不存在对应的key, 那么计数不会加1, 即:
     *     redis中存在的key-value里，有名为a1、a2的key，
     *     删除时，传的集合是a1、a2、a3，那么返回结果为2。
     *
     * @param keys
     *            要删除的key集合
     * @return  删除了的key-value个数
     */
    long delete(Collection<String> keys);

    /**
     * 将key对应的value值进行序列化，并返回序列化后的value值。
     *
     * 注: 若不存在对应的key, 则返回null。
     * 注: dump时，并不会删除redis中的对应key-value。
     * 注: dump功能与restore相反。
     *
     * @param key
     *            要序列化的value的key
     * @return  序列化后的value值
     */
    byte[] dump(String key);

    /**
     * 将给定的value值，反序列化到redis中, 形成新的key-value。
     *
     * @param key
     *            value对应的key
     * @param value
     *            要反序列的value值。
     *            注: 这个值可以由{@link this#dump(String)}获得
     * @param timeToLive
     *            反序列化后的key-value的存活时长
     * @param unit
     *            timeToLive的单位
     *
     * @throws RedisSystemException
     *             如果redis中已存在同样的key时，抛出此异常
     */
    void restore(String key, byte[] value, long timeToLive, TimeUnit unit);

    /**
     * redis中是否存在,指定key的key-value
     *
     * @param key
     *            指定的key
     * @return  是否存在对应的key-value
     */
    boolean hasKey(String key);

    /**
     * 给指定的key对应的key-value设置: 多久过时
     *
     * 注:过时后，redis会自动删除对应的key-value。
     * 注:若key不存在，那么也会返回false。
     *
     * @param key
     *            指定的key
     * @param timeout
     *            过时时间
     * @param unit
     *            timeout的单位
     * @return  操作是否成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 找到所有匹配pattern的key,并返回该key的结合.
     *
     * 提示:若redis中键值对较多，此方法耗时相对较长，慎用！慎用！慎用！
     *
     * @param pattern
     *            匹配模板。
     *            注: 常用的通配符有:
     *                 ?    有且只有一个;
     *                 *     >=0哥;
     *
     * @return  匹配pattern的key的集合。 可能为null。
     */
    Set<String> keys(String pattern);
}
