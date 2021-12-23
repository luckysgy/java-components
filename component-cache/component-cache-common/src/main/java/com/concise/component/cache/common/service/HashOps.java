package com.concise.component.cache.common.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 操作缓存 -- hash
 * 注释中大部分都是以redis进行举例子
 * @author shenguangyang
 * @date 2021-10-02 上午6:34
 */
public interface HashOps {
    /**
     * 向key对应的hash中，增加一个键值对entryKey-entryValue
     *
     * 注: 同一个hash里面，若已存在相同的entryKey， 那么此操作将丢弃原来的entryKey-entryValue，
     *     而使用新的entryKey-entryValue。
     *
     *
     * @param key 定位hash的key
     * @param entryKey 要向hash中增加的键值对里的 键
     * @param entryValue 要向hash中增加的键值对里的 值
     */
    <T> void put(String key, String entryKey, T entryValue);

    /**
     * 当key对应的hash中,不存在entryKey时，才(向key对应的hash中，)增加entryKey-entryValue
     * 否者，不进行任何操作
     *
     * @param key 定位hash的key
     * @param entryKey 要向hash中增加的键值对里的 键
     * @param entryValue 要向hash中增加的键值对里的 值
     * @return 操作是否成功。
     */
    <T> boolean putIfAbsent(String key, Object entryKey, T entryValue);

    /**
     * 获取到key对应的hash里面的对应字段的值
     *
     * 注: 若redis中不存在对应的key, 则返回null。
     *     若key对应的hash中不存在对应的entryKey, 也会返回null。
     *
     * @param key 定位hash的key
     * @param entryKey 定位hash里面的entryValue的entryKey
     * @return  key对应的hash里的entryKey对应的entryValue值
     */
    <T> T get(String key, Object entryKey);

    /**
     * 获取到key对应的hash(即: 获取到key对应的Map<HK, HV>)
     *
     * 注: 若redis中不存在对应的key, 则返回一个没有任何entry的空的Map(，而不是返回null)。
     *
     * @param key 定位hash的key
     * @return  key对应的hash。
     */
    <T> Map<String, T> getAll(String key);

    /**
     * 批量获取(key对应的)hash中的entryKey的entryValue
     *
     * 注: 若hash中对应的entryKey不存在，那么返回的对应的entryValue值为null
     * 注: redis中key不存在，那么返回的List中，每个元素都为null。
     *     追注: 这个List本身不为null, size也不为0， 只是每个list中的每个元素为null而已。
     *
     * @param key 定位hash的key
     * @param entryKeys 需要获取的hash中的字段集
     * @return  hash中对应entryKeys的对应entryValue集
     */
    <T> List<T> multiGet(String key, Collection<Object> entryKeys);

    /**
     * (批量)删除(key对应的)hash中的对应entryKey-entryValue
     *
     * 注: 1、若redis中不存在对应的key, 则返回0;
     *     2、若要删除的entryKey，在key对应的hash中不存在，在count不会+1, 如:
     *                 put("ds", "name", "邓沙利文");
     *                 put("ds", "birthday", "1994-02-05");
     *                 put("ds", "hobby", "女");
     *                 则调用delete("ds", "name", "birthday", "hobby", "non-exist-entryKey")
     *                 的返回结果为3
     * 注: 若(key对应的)hash中的所有entry都被删除了，那么该key也会被删除
     *
     * @param key 定位hash的key
     * @param entryKeys 定位要删除的entryKey-entryValue的entryKey
     * @return 删除了对应hash中多少个entry
     */
    long delete(String key, Object... entryKeys);

    /**
     * 查看(key对应的)hash中，是否存在entryKey对应的entry
     *
     * 注: 若redis中不存在key,则返回false。
     * 注: 若key对应的hash中不存在对应的entryKey, 也会返回false。
     *
     * @param key 定位hash的key
     * @param entryKey 定位hash中entry的entryKey
     * @return  hash中是否存在entryKey对应的entry.
     */
    boolean exists(String key, String entryKey);

    /**
     * 增/减(hash中的某个entryValue值) 整数
     *
     * 注: 负数则为减。
     * 注: 若key不存在，那么会自动创建对应的hash,并创建对应的entryKey、entryValue,entryValue的初始值为increment。
     * 注: 若entryKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     *     抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key 用于定位hash的key
     * @param entryKey 用于定位entryValue的entryKey
     * @param increment 增加多少
     * @return  增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    long incrementBy(String key, Object entryKey, long increment);

    /**
     * 增/减(hash中的某个entryValue值) 浮点数
     *
     * 注: 负数则为减。
     * 注: 若key不存在，那么会自动创建对应的hash,并创建对应的entryKey、entryValue,entryValue的初始值为increment。
     * 注: 若entryKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     *     抛出org.springframework.data.redis.RedisSystemException
     * 注: 因为是浮点数， 出现精度问题。
     *     追注: 本人简单测试了几组数据，暂未出现精度问题。
     *
     * @param key 用于定位hash的key
     * @param entryKey 用于定位entryValue的entryKey
     * @param increment 增加多少
     * @return  增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    double incrementByFloat(String key, Object entryKey, double increment);

    /**
     * 获取(key对应的)hash中的所有entryKey
     *
     * 注: 若key不存在，则返回的是一个空的Set(，而不是返回null)
     *
     * @param key 定位hash的key
     * @return  hash中的所有entryKey
     */
    Set<Object> keys(String key);

    /**
     * 获取(key对应的)hash中的所有entryValue
     *
     * 注: 若key不存在，则返回的是一个空的List(，而不是返回null)
     *
     * @param key 定位hash的key
     * @return  hash中的所有entryValue
     */
    <T> List<T> values(String key);
}
