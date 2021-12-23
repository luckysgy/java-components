package com.concise.component.core.thread;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 对Spring的ThreadPoolTaskExecutor进行增强
 * @see TtlThreadPoolExecutor
 * @author shenguangyang
 * @date 2021-10-09 8:16
 */
public class TtlThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {
    @Override
    public void execute(Runnable command) {
        Runnable ttlRunnable = TtlRunnable.get(command);
        super.execute(ttlRunnable);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Callable ttCallable = TtlCallable.get(task);
        return super.submit(ttCallable);
    }

    @Override
    public Future<?> submit(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submit(ttlRunnable);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submitListenable(ttlRunnable);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        Callable ttlCallable = TtlCallable.get(task);
        return super.submitListenable(ttlCallable);
    }
}
