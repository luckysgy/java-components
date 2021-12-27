package com.concise.component.datasource.mybatisplus.register;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.concise.component.core.utils.BeanRegistrationUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.datasource.mybatisplus.config.MyDruidDataSourceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Import;

/**
 * @author shenguangyang
 * @date 2021-12-27 7:35
 */
public class EnableMybatisPlusBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private volatile static Boolean isEnable = true;

    public static void setEnable(Boolean enable) {
        isEnable = enable;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        System.out.println("--------------");
//         beanDefinitionRegistry.removeBeanDefinition(DruidDataSourceAutoConfigure.class.getName());
//        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(
//                beanDefinitionRegistry, StringUtils.uncapitalize(MyDruidDataSourceConfig.class.getSimpleName()),
//                MyDruidDataSourceConfig.class
//        );
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
