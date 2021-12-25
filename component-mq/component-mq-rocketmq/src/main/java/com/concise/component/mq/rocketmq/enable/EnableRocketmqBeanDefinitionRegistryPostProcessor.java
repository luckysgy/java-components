package com.concise.component.mq.rocketmq.enable;

import com.concise.component.core.utils.ReflectUtils;
import com.concise.component.core.utils.SpringUtils;
import com.concise.component.core.utils.StringUtils;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.Set;

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
        beanDefinitionRegistry.removeBeanDefinition("org.apache.rocketmq.spring.autoconfigure.ListenerContainerConfiguration");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
