package com.concise.component.lock.service;

/**
 * 分布式锁服务
 * @author shenguangyang
 * @date 2021-10-01 下午3:04
 */
public abstract class LockService {
    public abstract void lock(String key);
    public abstract void unlock(String key);
}
