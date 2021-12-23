package com.concise.component.feign.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;

/**
 * Description: feign bean工厂后置处理器
 *
 * @author shenguangyang
 * @date 2021/05/28
 */
public class FeignBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        FeignHttpClientProperties bean = configurableListableBeanFactory.getBean(FeignHttpClientProperties.class);
        bean.setConnectionTimeout(100000);
        bean.setConnectionTimerRepeat(100000);
    }
}
