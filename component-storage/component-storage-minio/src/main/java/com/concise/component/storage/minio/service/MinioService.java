package com.concise.component.storage.minio.service;

import cn.hutool.core.util.RandomUtil;
import com.concise.component.core.exception.Assert;
import com.concise.component.core.utils.file.MimetypesUtils;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.StorageProperties;
import com.concise.component.storage.common.registerstoragemanage.StorageManage;
import com.concise.component.storage.common.registerstoragemanage.StorageManageHandler;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.common.storagetype.ConditionalOnStorageType;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import com.concise.component.storage.common.url.UrlTypesEnum;
import com.concise.component.storage.minio.utils.MinioUtils;
import com.concise.component.util.file.FileUtils;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 为minio 设置 nginx 代理: http://docs.minio.org.cn/docs/master/setup-nginx-proxy-with-minio
 * @author shenguangyang
 * @date 2021/7/17 13:41
 */
@Component
@ConditionalOnStorageType(type = StorageTypesEnum.MINIO)
public class MinioService implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    private final StorageProperties storageProperties;

    public MinioService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public <T extends StorageManage> void uploadText(Class<T> storageManage, String text, String objectName) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        MinioUtils.uploadFile(bucketName, inputStream, "text/plain", objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> void uploadFile(Class<T> storageManage, InputStream inputStream, String contentType, String objectName) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        MinioUtils.uploadFile(bucketName, inputStream, contentType, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> void uploadDir(Class<T> storageManage, String dirPath) {
        List<String> allFileList = FileUtils.getAllFile(dirPath, false, null);
        dirPath = dirPath.replace("\\", "/");

        if (!dirPath.endsWith("/")) {
            dirPath = dirPath + "/";
        }

        for (String filePath : allFileList) {
            String path = filePath.replace("\\", "/").replace(dirPath, "");

            // 针对win路径进行处理
            if (path.contains(":")) {
                path = path.substring(path.lastIndexOf(":") + 1);
            }

            String objectName = path;
            try (InputStream inputStream = new FileInputStream(filePath)) {
                uploadFile(storageManage, inputStream, MimetypesUtils.getInstance().getMimetype(FileUtils.getName(filePath)), objectName);
            } catch (Exception e) {
                log.error("error: ", e);
            }
        }
    }

    @Override
    public <T extends StorageManage> String getPresignedObjectUrl(Class<T> storageManage, String objectName, UrlTypesEnum urlTypes) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        String url = MinioUtils.getFileUrl(bucketName, objectNamePre + objectName, Method.GET);
        Assert.notEmpty(url, "获取url失败");
        if (storageProperties.getUrl().getProxy()) {
            if (UrlTypesEnum.INTERNAL.equals(urlTypes)) {
                String internal = storageProperties.getUrl().getInternal();
                return internal + url.replace(storageProperties.getMinio().getEndpoint(), "");
            } else {
                String external = storageProperties.getUrl().getExternal();
                return external + url.replace(storageProperties.getMinio().getEndpoint(), "");
            }
        }
        return url;
    }

    @Override
    public <T extends StorageManage> String getPermanentObjectUrl(Class<T> storageManage, String objectName, UrlTypesEnum urlTypes) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        if (storageProperties.getUrl().getProxy()) {
            if (UrlTypesEnum.INTERNAL.equals(urlTypes)) {
                String internal = storageProperties.getUrl().getInternal();
                return StringUtils.join(internal, bucketName, "/", objectNamePre + objectName);
            } else {
                String external = storageProperties.getUrl().getExternal();
                return StringUtils.join(external, bucketName, "/", objectNamePre + objectName);
            }
        }
        String urlPre = storageProperties.getMinio().getEndpoint();
        urlPre = urlPre.replace("https://", "")
                .replace("http://", "");
        return StringUtils.join("http://", urlPre , bucketName, "/" + objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> InputStream getFile(Class<T> storageManage, String objectName) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        return MinioUtils.getFile(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> Boolean createBucket(Class<T> storageManage, Boolean randomSuffix) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        try {
            if (randomSuffix != null && randomSuffix) {
                MinioUtils.createBucket( bucketName + "-" + RandomUtil.randomString(8));
            } else {
                MinioUtils.createBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("createBucket::bucketName = [{}] message = {}",objectNamePre, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public <T extends StorageManage> void deleteObjects(Class<T> storageManage, List<String> objectNameList) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        List<String> objectNames = new ArrayList<>(objectNameList.size());
        for (String objectName : objectNameList) {
            objectNames.add(objectNamePre + objectName);
        }
        MinioUtils.deleteObjects(bucketName, objectNames);
    }

    @Override
    public <T extends StorageManage> void deleteObject(Class<T> storageManage, String objectName) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        MinioUtils.deleteObject(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> boolean objectExist(Class<T> storageManage, String objectName) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        return MinioUtils.objectExist(bucketName, objectNamePre + objectName);
    }
}
