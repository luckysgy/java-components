package com.concise.component.mq.rocketmq.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 使能rocketmq
 * @author shenguangyang
 * @date 2021-12-25 12:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EnableRocketmqRegistrar.class})
public @interface EnableRocketmq {
    /**
     * 是否使能, 默认使能
     * @return
     */
    boolean value() default true;
}
