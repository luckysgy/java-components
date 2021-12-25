package com.concise.component.mq.common.listener;

import com.concise.component.core.utils.UUIDUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * mq监听者条件类
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Conditional(MqListenerCondition.class)
public @interface ConditionalOnMqListener {
    String[] basePackages() default {};
    Class<? extends MqListener> value();
}
