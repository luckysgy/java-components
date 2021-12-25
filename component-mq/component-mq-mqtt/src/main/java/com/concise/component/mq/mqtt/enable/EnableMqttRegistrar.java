package com.concise.component.mq.mqtt.enable;

import com.concise.component.core.utils.BeanRegistrationUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.listener.MqListenerScanRegister;
import com.concise.component.mq.mqtt.MqttMainConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author shenguangyang
 * @date 2021-12-25 12:57
 */
public class EnableMqttRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger log = LoggerFactory.getLogger(MqListenerScanRegister.class);


    /**
     * 是否被执行过
     * 如果调用层启动类没有指定 {@link EnableMqtt} 则会先执行且只执行一遍 {@link MqttMainConfig} 上的{@link EnableMqtt}注解，然后执行本类
     * 如果调用层启动类指定 {@link EnableMqtt},则会先执行且只执行一遍, 然后执行本类
     * 使用 {@link #isExecuted} 作为标记
     */
    private static Boolean isExecuted = false;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            if (isExecuted) {
                return;
            }
            isExecuted = true;
            Boolean enableMqttValue = getEnableMqttValue(importingClassMetadata);
            EnableMqttBeanDefinitionRegistryPostProcessor.setEnable(enableMqttValue);
            String beanId = StringUtils.uncapitalize(EnableMqttBeanDefinitionRegistryPostProcessor.class.getSimpleName());
            BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, beanId, EnableMqttBeanDefinitionRegistryPostProcessor.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Boolean getEnableMqttValue(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableMqtt.class.getName());
        return (Boolean) attributes.get("value");
    }
}
