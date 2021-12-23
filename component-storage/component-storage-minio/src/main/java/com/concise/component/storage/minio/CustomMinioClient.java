package com.concise.component.storage.minio;

import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import io.minio.messages.Part;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 重写minio方法，实现断点续传
 * 最近开发功能需要大文件上传，正常会在业务代码中封装一层minio的上传，为了避免中转，实现了minio直传的方式。
 * 实现过程中也确实碰到了不少坑，只能查询文档，本着开箱即用，简单方便的原则，java版本的Minio sdk默认是不
 * 允许单独调用分片的相关方法，但是升级到8.0.3后可以通过继承MinioClient实现分片方法的使用，后来又碰到
 * 官方Minio版本的bug，提交issue，还好Minio github开发者处理非常迅捷，再次感谢，2021-02-04修复的，
 * 使用的开发者需要注意下。 大致描述下流程：
 *
 * 1. 用户调用初始化接口，后端调用minio初始化，得到uploadId，生成每个分片的minio上传url
 * 2. 用户调用对应分片的上传地址，多次上传会覆盖
 * 3. 调用完成接口，后端查询所有上传的分片并合并
 */
public class CustomMinioClient extends MinioClient {

    protected CustomMinioClient(MinioClient client) {
        super(client);
    }

    public String initMultiPartUpload(String bucket, String region, String object, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ServerException, jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException, XmlParserException, InvalidResponseException, ErrorResponseException, io.minio.errors.InternalException, io.minio.errors.InsufficientDataException, io.minio.errors.ServerException {
        CreateMultipartUploadResponse response = this.createMultipartUpload(bucket, region, object, headers, extraQueryParams);

        return response.result().uploadId();
    }

    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ServerException, jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException, XmlParserException, InvalidResponseException, ErrorResponseException, io.minio.errors.InternalException, io.minio.errors.InsufficientDataException, io.minio.errors.ServerException {

        return this.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }

    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException, InvalidResponseException, InternalException, InvalidKeyException, io.minio.errors.InternalException, io.minio.errors.InsufficientDataException, io.minio.errors.ServerException {
        return this.listParts(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }
}