package com.concise.component.storage.common.service;

import com.concise.component.storage.common.config.StorageProperties;
import com.concise.component.storage.common.enums.UrlTypes;
import com.concise.component.storage.common.expand.StorageBucketName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 存储服务公共接口
 * @author shenguangyang
 * @date 2021/7/17 13:36
 */
public abstract class StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    protected StorageProperties storageProperties;

    public StorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    /**
     * 上传文本
     * @param text 文本内容
     * @param objectName 对象名 xx/yy/zz/fileName.text
     */
    public abstract <T extends StorageBucketName> void uploadText(Class<T> bucketNameClass, String text, String objectName) throws Exception;

    /**
     * 上传文件
     */
    public abstract <T extends StorageBucketName> void uploadFile(Class<T> bucketNameClass, InputStream inputStream, String contentType, String objectName) throws Exception;

    /**
     * 通过对象名获取永久url
     *
     * 1. oss
     * 如果配置使能nginx代理，则获取持久链接的前缀，就是url中的lan或者wan
     * eg：http://127.0.0.1:9090/files/bucketName/objctName
     * nginx相关配置如下,这个配置相当访问minio文件格式的地址转为访问oss格式的地址
     * <code>
     *     # 配置中使用 .* 表示0个或多个任意字段，用 () 括起来，可以在location中用$1、$2等获取。
     *     # 注意，使用这种方式，location内部不能包含if语句，否则proxy_pass不会生效
     *     # 浏览器访问http://127.0.0.1:9090/files/ai-approve-bucket/2021/07/17/test.log
     *     location ~ /files/(.*?)/(.*)$ {
     *     	    proxy_pass https://$1.oss-cn-beijing.aliyuncs.com/$2;
     *     }
     * </code>
     *
     * 如果关掉了nginx代理, 则{@link UrlTypes} 不生效,获取到的持久链接格式如下
     * eg: https://bucketName.oss-cn-beijing.aliyuncs.com/objectName
     *
     * 2. minio
     * 获取持久链接的前缀，就是url中的lan或者wan
     *
     * @apiNote 需要设置对象存储服务器上的桶为公共读，否则生成的链接是不可读额
     * @param objectName 对象名
     * @param urlTypes url类型,内网访问还是外网访问
     * @return 对象的url
     */
    public abstract <T extends StorageBucketName> String getFilePermanentUrl(Class<T> bucketNameClass, String objectName, UrlTypes urlTypes);

    /**
     * 获取文件
     * @param objectName 对象名
     * @return
     */
    public abstract <T extends StorageBucketName> InputStream getFile(Class<T> bucketNameClass, String objectName);

    /**
     * 创建桶
     * @param bucketNameClass 桶的名字
     * @param randomSuffix 是否使能随机后缀,防止桶名存在
     */
    public abstract <T extends StorageBucketName> Boolean createBucket(Class<T> bucketNameClass, Boolean randomSuffix);
}
