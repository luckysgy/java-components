package com.concise.component.storage.common;

import com.concise.component.core.utils.UrlUtils;
import com.concise.component.storage.common.storagetype.StorageEnableType;
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

    /**
     * 是否启用文件存储,默认不启动
     */
    private Boolean enable = false;

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
        url.setInternal(UrlUtils.addEndSlash(url.getInternal()));
        url.setExternal(UrlUtils.addEndSlash(url.getExternal()));
        if (StorageEnableType.isUsed(minio.getStorageType())) {
            minio.setEndpoint(UrlUtils.addEndSlash(minio.getEndpoint()));
        } else if (StorageEnableType.isUsed(oss.getStorageType())) {
            oss.setEndpoint(UrlUtils.addEndSlash(oss.getEndpoint()));
        }
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
        protected String internal;

        /**
         * 外网(广域网 Wide Area Network) url http://47.78.12.56
         */
        protected String external;

        /**
         * 是否通过nginx等第三方软件代理文件访问的url
         * 代理的url, 格式为 internal(或者external) + 桶名称 + 文件路径前缀 + 文件路径 + (签名)
         * eg: file1在demo桶下且路径为test/index.html, 则生成的代理url路径为
         *      http://127.0.0.1:9000/demo/test/index.html
         *
         * 需要nginx的请求路径添加桶名
         * @apiNote 路径前缀在这里配置{@link StorageBucketManage}
         */
        protected Boolean proxy = false;

        public Boolean getProxy() {
            return proxy;
        }

        public void setProxy(Boolean proxy) {
            this.proxy = proxy;
        }

        public int getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(int expiryTime) {
            StorageConstants.URL_EXPIRY_TIME = expiryTime;
            this.expiryTime = expiryTime;
        }

        public String getInternal() {
            return internal;
        }

        public void setInternal(String internal) {
            StorageConstants.URL_INTERNAL = internal;
            this.internal = internal;
        }

        public String getExternal() {
            return external;
        }

        public void setExternal(String external) {
            StorageConstants.URL_EXTERNAL = external;
            this.external = external;
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
        protected String endpoint;

        protected String bucketName;

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
            return StorageEnableType.isUsed(getStorageType());
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

        @Override
        public StorageTypesEnum getStorageType() {
            return StorageTypesEnum.OSS;
        }
    }
}