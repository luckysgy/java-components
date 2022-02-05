package com.concise.component.storage.minio.service;

import cn.hutool.http.HttpRequest;
import com.concise.component.storage.common.partupload.MultiPartUploadInit;
import com.concise.component.storage.common.registerstoragemanage.StorageManageHandler;
import com.concise.component.storage.common.registerstoragemanage.StorageManage;
import com.concise.component.storage.common.partupload.MultiPartUploadService;
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
    public <T extends StorageManage> MultiPartUploadInit init(Class<T> bucketNameClass, String objectName, int totalPart) {
        return MinioUtils.MultiPartUpload.init(StorageManageHandler.getBucketName(bucketNameClass), objectName, totalPart);
    }


    @Override
    public void uploadPart(String uploadUrl,String contentType, File file) {
        HttpRequest.put(uploadUrl)
                .contentType(contentType)
                .form("key", file)
                .execute();
    }

    @Override
    public <T extends StorageManage> void merge(Class<T> bucketNameClass, String objectName, String uploadId) {
        MinioUtils.MultiPartUpload.merge(StorageManageHandler.getBucketName(bucketNameClass),objectName,uploadId);
    }
}
