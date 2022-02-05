package com.concise.component.core;

import com.concise.component.core.utils.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author shenguangyang
 * @date 2021-12-25 12:57
 */
public class IniEnvRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final Logger log = LoggerFactory.getLogger(IniEnvRegistrar.class);

    private Environment environment;

    private static final String INI_ENV_FILE_PATH_DEFAULT = "/mnt/env.ini";
    private String iniEnvFilePath;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            this.iniEnvFilePath = environment.getProperty("iniEnvFilePath");
            if (StringUtils.isEmpty(this.iniEnvFilePath)) {
                this.iniEnvFilePath = INI_ENV_FILE_PATH_DEFAULT;
            }
            loadIniToSystemProperty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载env.ini到System.setProperty()中
     *
     * 目的是可以在springboot的application.yaml文件中可以通过 ${xxx} 取到值
     * 其中xxx就是env.ini中的key
     */
    public void loadIniToSystemProperty() throws IOException {

        /* 读取本地的ini环境配置 */
        File file = new File(iniEnvFilePath);
        if (!file.exists()) {
            log.warn("not exist [{}], this is an optional", iniEnvFilePath);
            return;
        }
        log.info("load [{}]", iniEnvFilePath);

        Wini ini = new Wini(file);
        Set<String> sectionNames = ini.keySet();
        for(String sectionName: sectionNames) {
            Ini.Section section = ini.get(sectionName);
            for (Map.Entry<String, String> entry : section.entrySet()) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }
}
