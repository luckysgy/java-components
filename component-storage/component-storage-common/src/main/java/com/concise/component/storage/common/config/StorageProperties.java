package com.concise.component.storage.common.config;

import com.concise.component.core.utils.StringUtils;
import com.concise.component.core.utils.UrlUtils;
import com.concise.component.storage.common.enums.StorageTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 存储配置
 *
 * @author shenguangyang
 * @date 2021/03/22
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "storage-server")
public class StorageProperties {
    private static final Logger log = LoggerFactory.getLogger(StorageProperties.class);

    //获取spring上下文实例
    @Autowired
    private ApplicationContext applicationContext;

    /** 是否被初始化标志位 */
    private static Boolean isInit = false;

    /**
     * 支持的存储类型
     */
    public static Map<String,Boolean> supportStorageTypes = new ConcurrentHashMap<>();

    /**
     * 是否启用文件存储,默认不启动
     */
    private Boolean enable = false;
    /**
     * 存储类型
     */
    private String type;

    /**
     * url配置
     */
    private Url url = new Url();

    /**
     * minio 配置
     */
    private Minio minio = new Minio();

    /**
     * oss配置
     */
    private Oss oss = new Oss();

    /**
     * 初始化方法,具有幂等性，只会被执行一次
     * @param storageType 存储类型
     */
    public static void init(String storageType) {
        if (isInit) {
            return;
        }
        log.info("init::storageType = [{}]",storageType);
        // 指定扫描的包名
        Reflections reflections = new Reflections("com.simplifydev.component.storage");
        //component是个接口，获取在指定包扫描的目录所有的实现类
        Set<Class<? extends ComponentConfig>> classes = reflections.getSubTypesOf(ComponentConfig.class);
        for (Class<? extends ComponentConfig> aClass : classes) {
            //遍历执行
            try {
                ComponentConfig component = aClass.newInstance();
                supportStorageTypes.put(component.getStorageType().getType(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 获取以存在的类型集合中是否含有用户传进来的类型
        String existType = "";
        Set<Map.Entry<String, Boolean>> entries = supportStorageTypes.entrySet();
        for (Map.Entry<String, Boolean> entry : entries) {
            if (entry.getKey().equals(storageType)) {
                existType = entry.getKey();
                break;
            }
        }

        // 校验类型是否存在
        if (StringUtils.isEmpty(existType)) {
            throw new RuntimeException("storage.type=" + storageType + " 不存在,支持的类型有 " +  supportStorageTypes.toString());
        }
        log.info("使用 {} 存储服务",existType);

        // 使能类型对应的存储服务
        supportStorageTypes.put(existType, true);
        isInit = true;
    }

    @PostConstruct
    public void init() {
//        Map<String, component> settings = applicationContext.getBeansOfType(component.class);
//        settings.forEach((key,value) ->{
//            //遍历执行
//            supportStorageTypes.put(value.getStorageType().getType(), false);
//        });
        url.setLan(UrlUtils.removeLastSlash(url.getLan()));
        url.setWan(UrlUtils.removeLastSlash(url.getWan()));
    }

    /**
     * url 配置，用于请求文件服务器，比如通过url下载文件，以及访问文件
     */
    public static class Url {
        /**
         * url过期时间单位是分钟, 用于具有时效性的链接
         */
        protected int expiryTime;
        /**
         * 内网url(局域网  Local Area Network) http://127.0.0.1
         */
        protected String lan;

        /**
         * 外网(广域网 Wide Area Network) url http://47.78.12.56
         */
        protected String wan;

        public int getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(int expiryTime) {
            StorageConstants.URL_EXPIRY_TIME = expiryTime;
            this.expiryTime = expiryTime;
        }

        public String getLan() {
            return lan;
        }

        public void setLan(String lan) {
            StorageConstants.URL_LAN = lan;
            this.lan = lan;
        }

        public String getWan() {
            return wan;
        }

        public void setWan(String wan) {
            StorageConstants.URL_WAN = wan;
            this.wan = wan;
        }
    }

    /**
     * 公共配置
     */
    @Data
    private abstract static class ComponentConfig {
        /**
         * 存储服务器的地址
         */
        private String endpoint;

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * 获取存储类型
         * @return 存储类型
         */
        public abstract StorageTypes getStorageType();

        /**
         * 判断是否使能
         * @return
         */
        public Boolean getEnable() {
            return supportStorageTypes.get(getStorageType().getType());
        }
    }

    /**
     * minio配置
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Component
    public static class Minio extends ComponentConfig {
        private String accessKey;
        private String secretKey;

        @Override
        public StorageTypes getStorageType() {
            return StorageTypes.MINIO;
        }
    }

    /**
     * oss配置
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Component
    public static class Oss extends ComponentConfig {
        private String accessKeyId;
        private String secretAccessKey;
        /**
         * 是否使能代理，如果使能nginx代理，则获取持久链接的前缀
         * 就是url中的lan或者wan
         *
         * {@link StorageService#getFilePermanentUrl(Class, String, UrlTypes)}
         */
        private Proxy proxy = new Proxy();

        @Data
        public static class Proxy {
            /** 是否使能代理 */
            private Boolean enable = false;
        }

        @Override
        public StorageTypes getStorageType() {
            return StorageTypes.OSS;
        }
    }
}