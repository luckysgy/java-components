package com.concise.component.lock.service;

import lombok.Getter;

/**
 * 分布式锁执行结果
 * @author shenguangyang
 * @date 2021-12-26 9:03
 */
@Getter
public class DistributedLockResult<T> {
    /**
     * 表示业务是否执行成功
     */
    private final Boolean success;
    /**
     * 是否获取到锁
     */
    private final Boolean isGetLock;
    /**
     * 执行结果
     */
    private final T result;

    public DistributedLockResult(Boolean success, Boolean isGetLock, T result) {
        this.success = success;
        this.isGetLock = isGetLock;
        this.result = result;
    }
}
