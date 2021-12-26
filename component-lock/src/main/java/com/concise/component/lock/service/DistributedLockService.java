package com.concise.component.lock.service;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分布式锁服务
 * @author shenguangyang
 * @date 2021-10-01 下午3:04
 */
public abstract class DistributedLockService {
    /**
     * 执行分布式锁
     * @param lockKey 锁的key
     * @param tryLockCount 尝试获取锁的次数
     * @param waitTime 尝试获取锁的超时时间, 单位是s
     */
    public abstract <T> DistributedLockResult<T> exec(String lockKey, long tryLockCount, long waitTime, Supplier<T> supplier);
}
