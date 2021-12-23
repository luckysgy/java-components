package com.concise.component.storage.oss.service;

import cn.hutool.core.util.RandomUtil;

import com.concise.component.core.utils.StringUtils;
import com.concise.component.storage.common.annotation.ConditionalOnStorageType;
import com.concise.component.storage.common.config.StorageProperties;
import com.concise.component.storage.common.enums.StorageTypes;
import com.concise.component.storage.common.enums.UrlTypes;
import com.concise.component.storage.common.expand.StorageBucketName;
import com.concise.component.storage.common.service.StorageService;
import com.concise.component.storage.oss.utils.OssUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author shenguangyang
 * @date 2021/7/17 13:41
 */
@Component
@ConditionalOnStorageType(type = StorageTypes.OSS)
public class OssService extends StorageService {
    private static final Logger log = LoggerFactory.getLogger(OssService.class);

    public OssService(StorageProperties storageProperties) {
        super(storageProperties);
    }

    @Override
    public <T extends StorageBucketName> void uploadText(Class<T> bucketNameClass, String text, String objectName) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        OssUtils.upload(inputStream, storageBucketName.getBucketName(), objectName, "text/plain");
    }

    @Override
    public <T extends StorageBucketName> void uploadFile(Class<T> bucketNameClass, InputStream inputStream, String contentType, String objectName) throws Exception {

    }

    @Override
    public <T extends StorageBucketName> String getFilePermanentUrl(Class<T> bucketNameClass, String objectName, UrlTypes urlTypes) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        String bucketName = storageBucketName.getBucketName();
        StorageProperties.Oss oss = storageProperties.getOss();
        Boolean enableProxy = oss.getProxy().getEnable();
        // 使能oss代理
        if (enableProxy) {
            if (UrlTypes.WAN.equals(urlTypes)) {
                String wan = storageProperties.getUrl().getWan();
                return StringUtils.join(wan, "/", bucketName,"/", objectName);
            } else {
                String lan = storageProperties.getUrl().getLan();
                return StringUtils.join(lan, "/", bucketName, "/", objectName);
            }
        } else {
            String endpoint = oss.getEndpoint();
            endpoint = endpoint.replace("https://", "")
                    .replace("http://", "");
            return StringUtils.join("https://", bucketName, ".", endpoint, "/", objectName);
        }
    }

    @Override
    public <T extends StorageBucketName> InputStream getFile(Class<T> bucketNameClass, String objectName) {
        return null;
    }

    @Override
    public <T extends StorageBucketName> Boolean createBucket(Class<T> bucketNameClass, Boolean randomSuffix) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        String bucketName = storageBucketName.getBucketName();
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
}
