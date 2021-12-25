package com.concise.component.mq.common.listener;

import com.concise.component.core.utils.StringUtils;
import com.concise.component.mq.common.MqCommonMainConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenguangyang
 * @date 2021-12-24 22:05
 */
public class MqListenerScanRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
    private static final Logger log = LoggerFactory.getLogger(MqListenerScanRegister.class);

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    private Environment environment;

    /**
     * 是否被执行过, 如果调用层启动类没有指定 {@link MqListenerScan} 则会先执行且只执行一遍
     * {@link MqCommonMainConfig} 上的{@link MqListenerScan}注解，执行本类
     *
     * 如果调用层启动类指定 {@link MqListenerScan},则会先执行且只执行一遍, 执行本类
     * 使用 {@link #isExecuted} 作为标记
     */
    private static Boolean isExecuted = false;

    private static final String MQ_LISTENER_INTERFACE_NAMES = MqListener.class.getName();

    /**
     * 移除注册的map, key: 类名, value: 移除标记(true)
     */
    private final Map<String, Boolean> removeRegisterMap = new ConcurrentHashMap<>();

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            if (isExecuted) {
                return;
            }
            isExecuted = true;
            // 获取全部子类
            Set<Class<? extends MqListener>> mqListenerAllSub = getMqListenerSub(registry);
            // 获取扫描的包路径
            Set<String> basePackages = getBasePackages(importingClassMetadata);
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            scanner.setResourceLoader(this.resourceLoader);
            // 接口不会被扫描，其子类会被扫描出来
            scanner.addIncludeFilter(new AssignableTypeFilter(MqListener.class));

            // 获取使能的listener
            Set<Class<? extends MqListener>> listener = getListener(importingClassMetadata);
            for (String basePackage : basePackages) {
                Set<BeanDefinition> candidateComponents = scanner
                        .findCandidateComponents(basePackage);
                for (BeanDefinition candidateComponent : candidateComponents) {
                    if (candidateComponent instanceof AnnotatedBeanDefinition) {
//                        AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
//                        AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                        registerEnableMqListener(registry, mqListenerAllSub, listener);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 {@link MqListener} 全部子类
     * @param registry
     * @return
     * @throws ClassNotFoundException
     */
    private Set<Class<? extends MqListener>> getMqListenerSub(BeanDefinitionRegistry registry) throws ClassNotFoundException {
        // 获取全部子类
        Set<Class<? extends MqListener>> mqListenerAllSub = new HashSet<>();

        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            if (beanDefinition instanceof ScannedGenericBeanDefinition) {
                ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
                String[] interfaceNames = scannedGenericBeanDefinition.getMetadata().getInterfaceNames();
                for (String interfaceName : interfaceNames) {
                    if (MQ_LISTENER_INTERFACE_NAMES.equals(interfaceName)) {
                        mqListenerAllSub.add((Class<? extends MqListener>) Class.forName(beanDefinition.getBeanClassName()));
                    }
                }
            }
        }
        return mqListenerAllSub;
    }

    public void registerEnableMqListener(BeanDefinitionRegistry registry, Set<Class<? extends MqListener>> mqListenerAllSub,
                                         Set<Class<? extends MqListener>> listener)  {
        if (listener.isEmpty()) {
            for (Class<? extends MqListener> mqListenerSub : mqListenerAllSub) {
                removeRegisterMqListenerSub(registry, mqListenerSub);
            }
        }
        for (Class<? extends MqListener> enableMqListenerSub : listener) {
            for (Class<? extends MqListener> mqListenerSub : mqListenerAllSub) {
                if (!mqListenerSub.getName().equals(enableMqListenerSub.getName())) {
                    removeRegisterMqListenerSub(registry, mqListenerSub);
                }
            }
        }
    }

    private void removeRegisterMqListenerSub(BeanDefinitionRegistry registry, Class<? extends MqListener> mqListenerSub) {
        String beanId = StringUtils.uncapitalize(mqListenerSub.getSimpleName());
        if (removeRegisterMap.get(beanId) != null) {
            return;
        }
        registry.removeBeanDefinition(beanId);
        removeRegisterMap.put(beanId, true);
        log.info("not enable mqListenerSub: {}", beanId);
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata().getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(),
                                    MqListenerScanRegister.this.classLoader);
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: " + beanDefinition.getMetadata().getClassName(), ex);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(MqListenerScan.class.getName());
        Set<String> basePackages = new HashSet<>();
//        for (Class<?> clazz : (Class[]) attributes.get("listener")) {
//            basePackages.add(ClassUtils.getPackageName(clazz));
//        }

        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    protected Set<Class<? extends MqListener>> getListener(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(MqListenerScan.class.getName());
        Set<Class<? extends MqListener>> listener = new HashSet<>();
        for (Class<? extends MqListener> aClass : (Class[]) attributes.get("listener")) {
            listener.add(aClass);
        }

        return listener;
    }

}
