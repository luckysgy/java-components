package com.concise.component.storage.common.registerbucketmanage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shenguangyang
 * @date 2021-12-25 20:50
 */
public class StorageBucketManageSubScanRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
    private static final Logger log = LoggerFactory.getLogger(StorageBucketManageSubScanRegister.class);

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    private Environment environment;

    /**
     * 使用 {@link #isExecuted} 作为标记
     */
    private static Boolean isExecuted = false;


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
            Set<StorageBucketManage> storageBucketManageAllSub = getStorageBucketNameSub(registry);
            for (StorageBucketManage storageBucketManage : storageBucketManageAllSub) {
                StorageBucketManageHandler.addStorageBucketSub(storageBucketManage);
                log.info("register storageBucketName: {}", storageBucketManage.getClass().getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 {@link StorageBucketManage} 全部子类
     * @param registry
     * @return
     * @throws ClassNotFoundException
     */
    private Set<StorageBucketManage> getStorageBucketNameSub(BeanDefinitionRegistry registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 获取全部子类
        Set<StorageBucketManage> storageBucketManageAllSub = new HashSet<>();

        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            if (beanDefinition instanceof ScannedGenericBeanDefinition) {
                ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
                String[] interfaceNames = scannedGenericBeanDefinition.getMetadata().getInterfaceNames();
                for (String interfaceName : interfaceNames) {
                    if (interfaceName.equals(StorageBucketManage.class.getName())) {
                        storageBucketManageAllSub.add((StorageBucketManage) Class.forName(beanDefinition.getBeanClassName()).newInstance());
                    }
                }
            }
        }
        return storageBucketManageAllSub;
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
                                    StorageBucketManageSubScanRegister.this.classLoader);
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


}
