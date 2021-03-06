package com.concise.component.mq.rocketmq.enable;

import com.concise.component.mq.common.enable.MqEnable;
import com.concise.component.mq.common.properties.MqType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * BeanDefinitionRegistryPostProcessor 后置处理器, 这里用于判断是否使能rocketmq
 * 如果不使能则移除, 相关自动配置类
 * @author shenguangyang
 * @date 2021-12-25 13:26
 */
public class EnableRocketmqBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private volatile static Boolean isEnable = true;

    public static void setEnable(Boolean enable) {
        isEnable = enable;
    }
    public EnableRocketmqBeanDefinitionRegistryPostProcessor() {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if (isEnable) {
            return;
        }
        if (MqEnable.isEnabled(MqType.ROCKETMQ)) {
            return;
        }
        beanDefinitionRegistry.removeBeanDefinition("org.apache.rocketmq.spring.autoconfigure.ListenerContainerConfiguration");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
