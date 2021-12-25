package com.concise.component.mq.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听器使能条件
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
public class MqListenerCondition implements Condition {
    private static final Logger log = LoggerFactory.getLogger(MqListenerCondition.class);
    private final Map<String, Boolean> enableCache = new ConcurrentHashMap<>();

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnMqListener.class.getName());
        if (attributes == null) {
            throw new RuntimeException("ConditionalOnMqListener.value == null");
        }
        Class<?> value = (Class<?>) attributes.get("value");
        if (enableCache.get(value.getName()) != null && enableCache.get(value.getName())) {
            return true;
        }

//        MqListenerScan mqListenerScan = MqListenerScanRegister.getEnableMqListener();
//        if (mqListenerScan != null) {
//            System.out.println("-----------------------------------------------------------");
//        }
//        for (Class<? extends MqListener> enable : enableMqListener.value()) {
//            if (enable.getName().equals(value.getName())) {
//                log.info("enable mq listener: {}", value.getName());
//                enableCache.put(value.getName(), Boolean.TRUE);
//                return true;
//            }
//        }
        return true;
    }
}
