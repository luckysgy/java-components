package com.concise.component.lock.annotationlock;

import java.lang.annotation.*;

/**
 * 用于标记分布式锁
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁的key值, 可使用SpEL传方法参数
     * @return key
     */
    String lockKey() default "defaultLock";

    /**
     * 尝试获取锁等待时间, 单位为s
     */
    long waitTime() default 3;

    /**
     * 尝试获取锁的次数,默认为5次
     */
    int tryLockCount() default 5;
}
