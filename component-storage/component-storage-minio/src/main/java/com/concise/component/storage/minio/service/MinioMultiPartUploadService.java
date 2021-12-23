package com.concise.component.storage.minio.service;

import cn.hutool.http.HttpRequest;
import com.concise.component.storage.common.entity.MultiPartUploadInit;
import com.concise.component.storage.common.expand.StorageBucketName;
import com.concise.component.storage.common.service.MultiPartUploadService;
import com.concise.component.storage.minio.utils.MinioUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author shenguangyang
 * @date 2021-10-16 16:48
 */
@Component
public class MinioMultiPartUploadService implements MultiPartUploadService {
    @Override
    public <T extends StorageBucketName> MultiPartUploadInit init(Class<T> bucketNameClass, String objectName, int totalPart) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        return MinioUtils.MultiPartUpload.init(storageBucketName.getBucketName(), objectName, totalPart);
    }


    @Override
    public void uploadPart(String uploadUrl,String contentType, File file) {
        HttpRequest.put(uploadUrl)
                .contentType(contentType)
                .form("key", file)
                .execute();
    }

    @Override
    public <T extends StorageBucketName> void merge(Class<T> bucketNameClass, String objectName, String uploadId) {
        StorageBucketName storageBucketName = StorageBucketName.getInstance(bucketNameClass);
        MinioUtils.MultiPartUpload.merge(storageBucketName.getBucketName(),objectName,uploadId);
    }
}
