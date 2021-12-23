package com.concise.component.cache.redis.service;

import com.concise.component.cache.common.key.KeyManager;
import com.concise.component.cache.common.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 * 
 * @author shenguangyang
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisService implements CacheService {
    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    private RedisKeyOps keyOps;

    @Autowired
    private RedisHashOps hashOps;

    @Autowired
    private RedisValueOps valueOps;

    @Autowired
    private RedisListOps listOps;

    @Override
    public KeyOps opsKey() {
        return keyOps;
    }

    @Override
    public ListOps opsList() {
        return listOps;
    }

    @Override
    public ValueOps opsValue() {
        return valueOps;
    }

    @Override
    public HashOps opsHash() {
        return hashOps;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存key
     * @param value 缓存的值
     * @param keyParams key参数
     */
    public <T> void setCacheObject(final KeyManager key, final T value, String... keyParams) {
        redisTemplate.opsForValue().set(key.getKey(keyParams), value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 减1
     * @param key 缓存键
     * @param keyParams key参数
     * @return 剩余数量
     */
    public Long decrement(KeyManager key, String keyParams) {
        return redisTemplate.opsForValue().decrement(key.getKey(keyParams));
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param keyParams key的参数
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final KeyManager key, final String... keyParams) {
        return expire(key.getKey(keyParams), key.getExpireSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 获得缓存的基本对象。
     * @param key key
     * @param keyParams key参数
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final KeyManager key, final String... keyParams) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key.getKey(keyParams));
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     * @param keyParams key参数
     */
    public boolean deleteObject(final KeyManager key, final String... keyParams) {
        return redisTemplate.delete(key.getKey(keyParams));
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     * 
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

//    /**
//     * list集合相关操作
//     */
//    public class ListOps {
//        /**
//         * 移除集合中右边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
//         * @param key 键
//         * @param seconds 超时等待时间
//         * @return list集合中的元素
//         */
//        public <T> T rightPop(final String key, final long seconds) {
//            return (T) redisTemplate.opsForList().rightPop(key, seconds, TimeUnit.SECONDS);
//        }
//
//        /**
//         * 在变量左边添加元素值。
//         * @param key 键
//         * @param value 值
//         */
//        public void leftPush(final String key, Object value) {
//            redisTemplate.opsForList().leftPush(key, value);
//        }
//    }


}
