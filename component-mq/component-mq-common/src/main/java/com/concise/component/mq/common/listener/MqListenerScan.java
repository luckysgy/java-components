package com.concise.component.mq.common.listener;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * mq监听者条件类
 * 扫描的包路径为: basePackages指定的路径
 * 启动类上面@EnableMqListener(value = {MqListener子类}) 类似 @ComponentScan(basePackages = {"com.migu.*"})
 * @author shenguangyang
 * @date 2021/7/17 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MqListenerScanRegister.class})
public @interface MqListenerScan {
    String[] basePackages() default {};
    Class<? extends MqListener>[] listener() default {};
}
