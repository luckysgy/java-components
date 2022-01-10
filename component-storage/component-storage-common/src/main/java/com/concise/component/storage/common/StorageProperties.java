package com.concise.component.storage.common;

import com.concise.component.core.utils.UrlUtils;
import com.concise.component.storage.common.registerbucket.StorageBucketManage;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.common.storagetype.StorageType;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Description: 存储配置
 *
 * @author shenguangyang
 * @date 2021/03/22
 */
@Data
@ConfigurationProperties(prefix = "storage-server")
public class StorageProperties {
    private static final Logger log = LoggerFactory.getLogger(StorageProperties.class);

    //获取spring上下文实例
    @Autowired
    private ApplicationContext applicationContext;

    /** 是否被初始化标志位 */
    private static Boolean isInit = false;

    /**
     * 是否启用文件存储,默认不启动
     */
    private Boolean enable = false;
    /**
     * 是否使用一个桶
     * 如果为true, 则使用minio / oss下面的bucketName
     * 如果为false, 则使用实现 {@link StorageBucketManage#getBucketName()}  中的桶名
     */
    private Boolean isOneBucket = true;
    /**
     * 是否初始化桶
     */
    private Boolean isInitBucket = true;
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

    @PostConstruct
    public void init() {
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
    public abstract static class ComponentConfig {
        /**
         * 存储服务器的地址
         */
        private String endpoint;

        private String bucketName;

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * 获取存储类型
         * @return 存储类型
         */
        public abstract StorageTypesEnum getStorageType();

        /**
         * 判断是否使能
         * @return
         */
        public Boolean getEnable() {
            return StorageType.isUsed(getStorageType());
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
        public StorageTypesEnum getStorageType() {
            return StorageTypesEnum.MINIO;
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
        public StorageTypesEnum getStorageType() {
            return StorageTypesEnum.OSS;
        }
    }
}