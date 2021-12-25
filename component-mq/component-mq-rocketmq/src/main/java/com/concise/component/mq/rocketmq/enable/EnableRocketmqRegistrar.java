package com.concise.component.mq.rocketmq.enable;

import com.concise.component.core.utils.BeanRegistrationUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.listener.MqListenerScanRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

/**
 * @author shenguangyang
 * @date 2021-12-25 12:57
 */
public class EnableRocketmqRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger log = LoggerFactory.getLogger(MqListenerScanRegister.class);


    /**
     * 是否被执行过, 如果调用层启动类没有指定 {@link EnableRocketmq} 则会先执行且只执行一遍
     * rocketmq组件中的主类 上的{@link EnableRocketmq}注解，然后执行本类
     *
     * 如果调用层启动类指定 {@link EnableRocketmq},则会先执行且只执行一遍, 执行本类
     * 使用 {@link #isExecuted} 作为标记
     */
    private static Boolean isExecuted = false;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            if (isExecuted) {
                return;
            }
            isExecuted = true;
            Boolean enableRocketmqValue = getEnableRocketmqValue(importingClassMetadata);
            EnableRocketmqBeanDefinitionRegistryPostProcessor.setEnable(enableRocketmqValue);
            String beanId = StringUtils.uncapitalize(EnableRocketmqBeanDefinitionRegistryPostProcessor.class.getSimpleName());
            BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, beanId, EnableRocketmqBeanDefinitionRegistryPostProcessor.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Boolean getEnableRocketmqValue(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableRocketmq.class.getName());
        return (Boolean) attributes.get("value");
    }
}
