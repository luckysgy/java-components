package com.concise.component.core.utils;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * bean 注册工具类
 * @author shenguangyang
 * @date 2021-12-25 16:10
 */
public class BeanRegistrationUtil {
    public static void registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanId, Class<?> targetClass) {
        if (!registry.containsBeanDefinition(beanId)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(targetClass);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.setBeanClass(targetClass);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(beanId, definition);
        }
    }
}
