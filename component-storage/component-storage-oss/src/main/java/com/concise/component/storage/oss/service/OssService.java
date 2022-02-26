package com.concise.component.storage.oss.service;

import cn.hutool.core.util.RandomUtil;
import com.concise.component.core.exception.Assert;
import com.concise.component.core.exception.BizException;
import com.concise.component.core.utils.MimetypesUtils;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.StorageProperties;
import com.concise.component.storage.common.registerstoragemanage.StorageManage;
import com.concise.component.storage.common.registerstoragemanage.StorageManageHandler;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.common.storagetype.ConditionalOnStorageType;
import com.concise.component.storage.common.storagetype.StorageTypesEnum;
import com.concise.component.storage.common.url.UrlTypesEnum;
import com.concise.component.storage.oss.utils.OssUtils;
import com.concise.component.util.file.FileUtils;
import com.google.common.collect.Lists;
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
 * @author shenguangyang
 * @date 2021/7/17 13:41
 */
@Component
@ConditionalOnStorageType(type = StorageTypesEnum.OSS)
public class OssService implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(OssService.class);
    /**
     * 批量删除组大小
     */
    private static final Integer DELETE_BATCH_GROUP_SIZE = 900;

    private final StorageProperties storageProperties;

    public OssService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public <T extends StorageManage> void uploadText(Class<T> storageManage, String text, String objectName) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        OssUtils.upload(inputStream, bucketName, objectNamePre + objectName, "text/plain");
    }

    @Override
    public <T extends StorageManage> void uploadFile(Class<T> storageManage, InputStream inputStream, String contentType, String objectName) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        OssUtils.upload(inputStream, bucketName, objectNamePre + objectName, contentType);
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
        String url = OssUtils.getAccessURL(bucketName, objectNamePre + objectName);
        Assert.notEmpty(url, "获取url失败");
        if (storageProperties.getUrl().getProxy()) {
            // 去掉桶名
            url = url.replace(storageProperties.getOss().getBucketName() + ".", "");
            if (UrlTypesEnum.INTERNAL.equals(urlTypes)) {
                String internal = storageProperties.getUrl().getInternal();
                return internal + storageProperties.getOss().getBucketName() + "/" + url.replace(storageProperties.getOss().getEndpoint(), "");
            } else {
                String external = storageProperties.getUrl().getExternal();
                return external + storageProperties.getOss().getBucketName() + "/" + url.replace(storageProperties.getOss().getEndpoint(), "");
            }
        }
        return url;
    }

    @Override
    public <T extends StorageManage> String getPermanentObjectUrl(Class<T> storageManage, String objectName, UrlTypesEnum urlTypes) {
        StorageProperties.Oss oss = storageProperties.getOss();
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        if (storageProperties.getUrl().getProxy()) {
            if (UrlTypesEnum.EXTERNAL.equals(urlTypes)) {
                String external = storageProperties.getUrl().getExternal();
                return StringUtils.join(external, bucketName, "/" ,objectNamePre + objectName);
            } else {
                String internal = storageProperties.getUrl().getInternal();
                return StringUtils.join(internal , bucketName, "/", objectNamePre + objectName);
            }
        }
        String urlPre = oss.getEndpoint();
        urlPre = urlPre.replace("https://", "")
                .replace("http://", "");
        return StringUtils.join("https://", bucketName, ".", urlPre , objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> InputStream getFile(Class<T> storageManage, String objectName) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        return OssUtils.getFile(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> List<String> getFilePathList(Class<T> storageManage, String pathPrefix) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);

        // 移除前后两个 /
        pathPrefix = StringUtils.removeEnd(pathPrefix, "/");
        pathPrefix = StringUtils.removeStart(pathPrefix, "/");
        return OssUtils.getFilePathList(bucketName, objectNamePre + pathPrefix);
    }

    @Override
    public <T extends StorageManage> Boolean createBucket(Class<T> storageManage, Boolean randomSuffix) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        try {
            if (randomSuffix != null && randomSuffix) {
                return OssUtils.createBucket(bucketName + "-" + RandomUtil.randomString(8));
            }
            return OssUtils.createBucket(bucketName);
        } catch (Exception e) {
            log.error("createBucket::bucketName = [{}] message = {}",bucketName, e.getMessage());
            return false;
        }
    }

    /**
     * 一次最大删除900左右文件
     * @param storageManage 桶
     * @param objectNameList 对象名集合
     * @param <T>
     * @throws Exception
     */
    @Override
    public <T extends StorageManage> void deleteObjects(Class<T> storageManage, List<String> objectNameList) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        List<String> objectNames = new ArrayList<>(objectNameList.size());
        for (String objectName : objectNameList) {
            objectNames.add(objectNamePre + objectName);
        }
        for (List<String> groupObjectNames : Lists.partition(objectNames, DELETE_BATCH_GROUP_SIZE)) {
            int deleteSize = OssUtils.deleteObjects(bucketName, groupObjectNames);
            if (deleteSize != groupObjectNames.size()) {
                throw new BizException("实际删除的文件数量: " + deleteSize + ", 目标删除文件数量: " + groupObjectNames.size());
            }
        }
    }

    @Override
    public <T extends StorageManage> void deleteObject(Class<T> storageManage, String objectName) throws Exception {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        OssUtils.deleteObject(bucketName, objectNamePre + objectName);
    }

    @Override
    public <T extends StorageManage> boolean objectExist(Class<T> storageManage, String objectName) {
        String bucketName = StorageManageHandler.getBucketName(storageManage);
        String objectNamePre = StorageManageHandler.getObjectNamePre(storageManage);
        return OssUtils.objectExist(bucketName, objectNamePre + objectName);
    }
}
