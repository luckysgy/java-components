package com.concise.component.storage.common.partupload;

import com.concise.component.storage.common.registerbucketmanage.StorageBucketManage;

import java.io.File;

/**
 * 分片上次服务
 * @author shenguangyang
 * @date 2021-10-16 16:39
 */
public interface MultiPartUploadService {
    /**
     * 初始化
     * @param bucketNameClass 桶名
     * @param objectName 对象名
     * @param totalPart 一共多少片
     */
    <T extends StorageBucketManage> MultiPartUploadInit init(Class<T> bucketNameClass, String objectName, int totalPart);

    /**
     * 上次分片
     * @param uploadUrl 上传的url
     * @param contentType 类型
     * @param file 文件
     */
    void uploadPart(String uploadUrl, String contentType, File file);

    <T extends StorageBucketManage> void merge(Class<T> bucketNameClass, String objectName, String uploadId);
}
