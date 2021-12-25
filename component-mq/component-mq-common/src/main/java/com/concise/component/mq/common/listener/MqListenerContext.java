package com.concise.component.mq.common.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mq上下文
 * @author shenguangyang
 * @date 2021-12-24 8:11
 */
public class MqListenerContext {
    public static Map<String, MqListener> mqListenerMap = new ConcurrentHashMap<>();
    /**
     * 注册监听者, 只有注册的舰艇在才会生效
     * @param listener
     * @param <T>
     */
    public static <T> void register(Class<T> listener) {

    }
}
