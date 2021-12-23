package com.concise.component.cache.common.service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 操作缓存 -- list
 * 注释中大部分都是以redis进行举例子
 * @author shenguangyang
 * @date 2021-10-02 上午7:07
 */
public interface ListOps {
    /**
     * 从左端推入元素进列表
     *
     * 注: 若redis中不存在对应的key, 那么会自动创建
     *
     * @param key 定位list的key
     * @param item 要推入list的元素
     * @return 推入后，(key对应的)list的size
     */
    <T> long leftPush(String key, T item);

    /**
     * 从左端批量推入元素进列表
     *
     * 注: 若redis中不存在对应的key, 那么会自动创建
     * 注: 这一批item中，先push左侧的, 后push右侧的
     *
     * @param key 定位list的key
     * @param items 要批量推入list的元素集
     * @return 推入后，(key对应的)list的size
     */
    long leftPushAll(String key, Object... items);

    /**
     * 从左端批量推入元素进列表
     *
     * 注: 若redis中不存在对应的key, 那么会自动创建
     * 注: 这一批item中，那个item先从Collection取出来，就先push哪个
     *
     * @param key 定位list的key
     * @param items 要批量推入list的元素集
     * @return 推入后，(key对应的)list的size
     */
    <T> long leftPushAll(String key, Collection<T> items);

    /**
     * 如果redis中存在key, 则从左端批量推入元素进列表;
     * 否则，不进行任何操作
     *
     * @param key 定位list的key
     * @param item 要推入list的项
     * @return  推入后，(key对应的)list的size
     */
    <T> long leftPushIfPresent(String key, T item);

    /**
     * 若key对应的list中存在pivot项, 那么将item放入第一个pivot项前(即:放在第一个pivot项左边);
     * 若key对应的list中不存在pivot项, 那么不做任何操作， 直接返回-1。
     *
     * 注: 若redis中不存在对应的key, 那么会自动创建
     *
     * @param key 定位list的key
     * @param item 要推入list的元素
     * @return 推入后，(key对应的)list的size
     */
    <T> long leftPush(String key, String pivot, T item);

    /**
     * 从list右侧推入元素
     */
    <T> long rightPush(String key, T item);

    /**
     * 从list右侧推入元素
     */
    long rightPushAll(String key, Object... items);

    /**
     * 从list右侧推入元素
     */
    <T> long rightPushAll(String key, Collection<T> items);

    /**
     * 与{@link #leftPushIfPresent(String, Object)} (String, Object)}类比即可， 不过是从list右侧推入元素
     */
    <T> long rightPushIfPresent(String key, T item);

    /**
     * 与{@link #leftPush(String, Object)} (String, String, Object)}类比即可， 不过是从list右侧推入元素
     */
    <T> long rightPush(String key, String pivot, T item);

    /**
     * 【非阻塞队列】 从左侧移出(key对应的)list中的第一个元素, 并将该元素返回
     *
     * 注: 此方法是非阻塞的， 即: 若(key对应的)list中的所有元素都被pop移出了，此时，再进行pop的话，会立即返回null
     * 注: 此方法是非阻塞的， 即: 若redis中不存在对应的key,那么会立即返回null
     * 注: 若将(key对应的)list中的所有元素都pop完了，那么该key会被删除
     *
     * @param key 定位list的key
     * @return  移出的那个元素
     */
    <T> T leftPop(String key);

    /**
     * 【阻塞队列】 从左侧移出(key对应的)list中的第一个元素, 并将该元素返回
     *
     * 注: 此方法是阻塞的， 即: 若(key对应的)list中的所有元素都被pop移出了，此时，再进行pop的话，
     *     会阻塞timeout这么久，然后返回null
     * 注: 此方法是阻塞的， 即: 若redis中不存在对应的key,那么会阻塞timeout这么久，然后返回null
     * 注: 若将(key对应的)list中的所有元素都pop完了，那么该key会被删除
     *
     * 提示: 若阻塞过程中， 目标key-list出现了，且里面有item了，那么会立马停止阻塞, 进行元素移出并返回
     *
     * @param key 定位list的key
     * @param timeout 超时时间
     * @param unit timeout的单位
     * @return  移出的那个元素
     */
    <T> T leftPop(String key, long timeout, TimeUnit unit);

    /**
     * 与{@link #leftPop(String)} (String)}类比即可， 不过是从list右侧移出元素
     */
    <T> T rightPop(String key);

    /**
     * 与{@link #leftPop(String)} (String, long, TimeUnit)}类比即可， 不过是从list右侧移出元素
     */
    <T> T rightPop(String key, long timeout, TimeUnit unit);

    /**
     * 【非阻塞队列】 从sourceKey对应的sourceList右侧移出一个item, 并将这个item推
     *              入(destinationKey对应的)destinationList的左侧
     *
     * 注: 若sourceKey对应的list中没有item了，则立马认为(从sourceKey对应的list中pop出来的)item为null,
     *     null并不会往destinationKey对应的list中push。
     *     追注: 此时，此方法的返回值是null。
     *
     * 注: 若将(sourceKey对应的)list中的所有元素都pop完了，那么该sourceKey会被删除。
     *
     * @param sourceKey 定位sourceList的key
     * @param destinationKey 定位destinationList的key
     * @return 移动的这个元素
     */
   <T> T rightPopAndLeftPush(String sourceKey, String destinationKey);
}
