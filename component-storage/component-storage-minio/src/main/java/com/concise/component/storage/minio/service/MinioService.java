package com.concise.component.storage.minio.service;

import cn.hutool.core.util.RandomUtil;
import com.concise.component.core.exception.Assert;
import com.concise.component.core.utils.MimetypesUtils;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.StorageProperties;
import com.concise.component.storage.common.registerbucketmanage.StorageBucketManage;
import com.concise.component.storage.common.registerbucketmanage.StorageBucketManageHandler;
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
    public <T extends StorageBucketManage> void uploadText(Class<T> storageBucket, String text, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        MinioUtils.uploadFile(bucketName, inputStream, "text/plain", objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> void uploadFile(Class<T> storageBucket, InputStream inputStream, String contentType, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        MinioUtils.uploadFile(bucketName, inputStream, contentType, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> void uploadDir(Class<T> storageBucket, String dirPath) {
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
                uploadFile(storageBucket, inputStream, MimetypesUtils.getInstance().getMimetype(FileUtils.getName(filePath)), objectName);
            } catch (Exception e) {
                log.error("error: ", e);
            }
        }
    }

    @Override
    public <T extends StorageBucketManage> String getPresignedObjectUrl(Class<T> storageBucket, String objectName, UrlTypesEnum urlTypes) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
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
    public <T extends StorageBucketManage> String getPermanentObjectUrl(Class<T> storageBucket, String objectName, UrlTypesEnum urlTypes) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
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
    public <T extends StorageBucketManage> InputStream getFile(Class<T> storageBucket, String objectName) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        return MinioUtils.getFile(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageBucketManage> Boolean createBucket(Class<T> storageBucket, Boolean randomSuffix) {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
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
    public <T extends StorageBucketManage> void deleteObjects(Class<T> storageBucket, List<String> objectNameList) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        List<String> objectNames = new ArrayList<>(objectNameList.size());
        for (String objectName : objectNameList) {
            objectNames.add(objectNamePre + objectName);
        }
        MinioUtils.deleteObjects(bucketName, objectNames);
    }

    @Override
    public <T extends StorageBucketManage> void deleteObject(Class<T> storageBucket, String objectName) throws Exception {
        String bucketName = StorageBucketManageHandler.getBucketName(storageBucket);
        String objectNamePre = StorageBucketManageHandler.getObjectNamePre(storageBucket);
        MinioUtils.deleteObject(bucketName, objectNamePre + objectName);
    }
}
