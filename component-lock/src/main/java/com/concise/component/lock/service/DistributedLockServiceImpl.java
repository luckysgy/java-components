package com.concise.component.lock.service;

import com.concise.component.core.entity.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * @author shenguangyang
 * @date 2021-12-26 9:05
 */
@Service
public class DistributedLockServiceImpl extends DistributedLockService {
    private static final String LOCK_KEY_PRE = "distributed_lock";
    private static final Logger log = LoggerFactory.getLogger(DistributedLockServiceImpl.class);

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Override
    public <T> DistributedLockResult<T> exec(String lockKey, long tryLockCount, long waitTime, Supplier<T> supplier) {
        // registryKey和lockKey自动冒号连接，最终key为REDIS_LOCK:lockKey，值为uuid
        Lock lock = redisLockRegistry.obtain(LOCK_KEY_PRE+ ":" + lockKey);
        T result = null;
        boolean ifLock = false;
        try {
            for(int i =0 ; i < tryLockCount; i++){
                ifLock = lock.tryLock(waitTime, TimeUnit.SECONDS);
                if (ifLock) {
                    break;
                }
            }
            // 可以获取到锁，说明当前没有线程在执行该方法
            if (ifLock) {
                try {
                    result = supplier.get();
                    return new DistributedLockResult<T>(true, true, result);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return new DistributedLockResult<T>(false, true, null);
                }
            } else {
                log.debug("线程[{}]未获取到锁，目前锁详情信息为：{}", Thread.currentThread().getName(), lock);
                return new DistributedLockResult<T>(true, false, null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new DistributedLockResult<T>(false, ifLock, result);
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                log.error("解锁dealAction出错:{}", e.getMessage());
            }
        }
    }
}
