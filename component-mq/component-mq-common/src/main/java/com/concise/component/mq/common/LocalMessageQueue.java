package com.concise.component.mq.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 本地消息队列
 * <code>
 * public class TestLocalMessageQueue {
 *     public static void main(String[] args) throws InterruptedException {
 *         LocalMessageQueue<String> myLocalMessageQueue = new MyLocalMessageQueue();
 *         CountDownLatch countDownLatch = new CountDownLatch(10);
 *         for (int i = 1; i <= 10; i++) {
 *             new Thread(() -> {
 *                 for (int j = 1; j <= 1000; j++) {
 *                     try {
 *                         myLocalMessageQueue.put(UUIDUtil.uuid());
 *                     } catch (InterruptedException e) {
 *                         e.printStackTrace();
 *                     }
 *                 }
 *                 countDownLatch.countDown();
 *             }, "" + i).start();
 *         }
 *
 *         new Thread(() -> {
 *             int count = 1;
 *             while (true) {
 *                 String data = myLocalMessageQueue.get();
 *                 if (data == null) {
 *                     if (myLocalMessageQueue.isStopPut()) {
 *                         break;
 *                     }
 *                     continue;
 *                 }
 *                 System.out.println(count++);
 *             }
 *         }).start();
 *         countDownLatch.await();
 *         myLocalMessageQueue.stopPut();
 *         TimeUnit.SECONDS.sleep(60);
 *     }
 * }
 * </code>
 * @author shenguangyang
 */
public abstract class LocalMessageQueue<T> {
    private volatile boolean running = true;
    private volatile BlockingQueue<T> queue;

    private BlockingQueue<T> getSingletonQueue() {
        if (queue == null) {
            synchronized (this) {
                if (queue == null) {
                    queue = createQueue();
                }
            }
        }
        return queue;
    }
    protected abstract BlockingQueue<T> createQueue();

    public void stopPut() {
        running = false;
    }

    /**
     * 当队列中没有数据时候, 判断该方法, 如果返回为false, 则相应的业务逻辑立刻退出
     * 循环
     * @return true 表示put已停止
     */
    public boolean isStopPut() {
        return !running;
    }

    public boolean put(T data) throws InterruptedException {
        return getSingletonQueue().offer(data, 2, TimeUnit.SECONDS);
    }

    /**
     * 在业务代码中不断调用
     * @return
     */
    public T get() {
        return getSingletonQueue().poll();
    }
}
