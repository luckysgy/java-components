package com.concise.component.mq.common.customconfig;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 如果自定义kafka配置文件, 需要添加如下注解才能实现根据配置文件中
 * 使能或不使能决定是否加载自定义的kafka配置类
 * @author shenguangyang
 * @date 2022-01-09 20:15
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(KafkaConfigCondition.class)
public @interface ConditionalOnKafkaConfig {
}
