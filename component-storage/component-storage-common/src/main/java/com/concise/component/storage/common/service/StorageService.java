package com.concise.component.storage.common.service;

import com.concise.component.storage.common.StorageProperties;
import com.concise.component.storage.common.registerstoragemanage.StorageManage;
import com.concise.component.storage.common.url.UrlTypesEnum;

import java.io.InputStream;
import java.util.List;

/**
 * 存储服务公共接口
 * @author shenguangyang
 * @date 2021/7/17 13:36
 */
public interface StorageService {
    /**
     * 上传文本
     * @param text 文本内容
     * @param objectName 对象名 xx/yy/zz/fileName.text
     */
    default <T extends StorageManage> void uploadText(Class<T> storageBucket, String text, String objectName) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * 上传文件
     */
    default <T extends StorageManage> void uploadFile(Class<T> storageBucket, InputStream inputStream, String contentType, String objectName) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * 上传文件夹
     */
    default <T extends StorageManage> void uploadDir(Class<T> storageBucket, String dirPath) {
        throw new UnsupportedOperationException();
    }

    /**
     * 通过对象名获取永久url
     *
     * 如果配置使能nginx代理，则获取持久链接的前缀，就是{@link StorageProperties#getUrl()} 中的内网或者外网配置
     * eg：http://127.0.0.1:9090/files/bucketName/objctName
     * @apiNote 生成的url是一个有时效性的
     * @param objectName 对象名
     * @param urlTypesEnum url类型,内网访问还是外网访问
     * @return 持久的url, url= 内网或者外网地址 + 桶名 + 对象名
     */
    default <T extends StorageManage> String getPermanentObjectUrl(Class<T> storageBucket, String objectName, UrlTypesEnum urlTypesEnum) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取预先签名的对象url
     * @apiNote 生成的url是通过桶名 + 对象名拼接而成, 因此需要将桶设置公共读才可以访问, 否则无权限访问
     * @param objectName 对象名
     * @param urlTypesEnum url类型,内网访问还是外网访问
     * @return 经过签名的url
     */
    default <T extends StorageManage> String getPresignedObjectUrl(Class<T> storageBucket, String objectName, UrlTypesEnum urlTypesEnum) {
        throw new UnsupportedOperationException();
    }
    /**
     * 获取文件
     * @param objectName 对象名
     * @return
     */
    default <T extends StorageManage> InputStream getFile(Class<T> storageBucket, String objectName) {
        throw new UnsupportedOperationException();
    }

    /**
     * 创建桶
     * @param storageBucket 桶的名字
     * @param randomSuffix 是否使能随机后缀,防止桶名存在
     */
    default <T extends StorageManage> Boolean createBucket(Class<T> storageBucket, Boolean randomSuffix) {
        throw new UnsupportedOperationException();
    }

    /**
     * 批量删除文件
     * @param storageBucket 桶
     * @param objectNameList 对象名集合
     * @param <T>
     * @return
     */
    default <T extends StorageManage> void deleteObjects(Class<T> storageBucket, List<String> objectNameList) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除文件
     * @param storageBucket 桶
     * @param objectName 对象名集合
     * @param <T>
     * @return
     */
    default <T extends StorageManage> void deleteObject(Class<T> storageBucket, String objectName) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * 判断对象是否存在
     * @param objectName 对象名
     * @param storageBucket 指定存储路径管理
     */
    default <T extends StorageManage> boolean objectExist(Class<T> storageBucket, String objectName) {
        throw new UnsupportedOperationException();
    }
}
