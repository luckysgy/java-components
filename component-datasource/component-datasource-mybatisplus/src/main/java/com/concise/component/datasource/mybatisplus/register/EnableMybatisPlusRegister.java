package com.concise.component.datasource.mybatisplus.register;

import com.concise.component.core.utils.BeanRegistrationUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.datasource.mybatisplus.config.EnableConfig;
import com.concise.component.datasource.mybatisplus.config.MyDruidDataSourceConfig;
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
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shenguangyang
 * @date 2021-12-25 20:50
 */
public class EnableMybatisPlusRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
    private static final Logger log = LoggerFactory.getLogger(EnableMybatisPlusRegister.class);

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
            boolean enableMybatisPlusValue = getEnableMybatisPlusValue(importingClassMetadata);
            if (!enableMybatisPlusValue) {
                BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry,
                        EnableConfig.class.getSimpleName(), EnableConfig.class);
                registry.removeBeanDefinition(StringUtils.uncapitalize(MyDruidDataSourceConfig.class.getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean getEnableMybatisPlusValue(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableMybatisPlus.class.getName());
        return (boolean) attributes.get("value");
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
                                    EnableMybatisPlusRegister.this.classLoader);
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
