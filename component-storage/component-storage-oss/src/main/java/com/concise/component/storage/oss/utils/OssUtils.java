package com.concise.component.storage.oss.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.model.*;

import com.concise.component.core.exception.BizException;
import com.concise.component.storage.common.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Description: oss辅助类
 *
 * @author shenguangyang
 * @date 2021/03/22
 */
@Component
public class OssUtils {

    private static final Logger log = LoggerFactory.getLogger(OssUtils.class);

    private static StorageProperties.Oss oss;
    private static StorageProperties.Url url;

    private static OSS ossClient;

    /**
     * 如果为 application/octet-stream 获取的图片链接只可以下载的
     * image/png：表示可以在线预览
     */
    private static final String CONTENT_TYPE = "application/octet-stream";

    public static void init(OSS ossClient, StorageProperties.Oss oss, StorageProperties.Url url) {
        OssUtils.ossClient = ossClient;
        OssUtils.oss = oss;
        OssUtils.url = url;
    }

    /**
     * 新建Bucket  --Bucket权限:私有
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     * */
    public static boolean createBucket(String bucketName){
        Bucket bucket = ossClient.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }

    /**
     * 文件上传,注意：在实际项目中，文件上传成功后，数据库中存储文件地址
     * 注：阿里云OSS文件上传官方文档链接：
     * https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11186623.6.749.11987a7dRYVSzn
     * @param uploadFile 上传的文件
     * @param bucketName 桶的名字  null或者空字符串则使用默认的桶名字
     * @param objectName 对象名字 xxx/zzzz/yyyy/fileName.jpg或者fileName.jpg
     */
    public static void upload(MultipartFile uploadFile, String bucketName, String objectName) {
        // 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 下面两行代码是重点坑：
         * 现在阿里云OSS 默认图片上传ContentType是image/jpeg
         * 也就是说，获取图片链接后，图片是下载链接，而并非在线浏览链接，
         * 因此，这里在上传的时候要解决ContentType的问题，将其改为image/jpg
         */
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(uploadFile.getContentType());

        //文件上传至阿里云OSS
        ossClient.putObject(bucketName, objectName, inputStream, meta);
    }

    /**
     * 文件上传,注意：在实际项目中，文件上传成功后，数据库中存储文件地址
     * 注：阿里云OSS文件上传官方文档链接：
     * https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11186623.6.749.11987a7dRYVSzn
     * @param inputStream 文件输入流
     * @param bucketName 桶的名字  null或者空字符串则使用默认的桶名字
     * @param objectName 对象名字 xxx/zzzz/yyyy/fileName.jpg或者fileName.jpg
     */
    public static void upload(InputStream inputStream, String bucketName, String objectName, String contentType) {
        /*
         * 下面两行代码是重点坑：
         * 现在阿里云OSS 默认图片上传ContentType是image/jpeg
         * 也就是说，获取图片链接后，图片是下载链接，而并非在线浏览链接，
         * 因此，这里在上传的时候要解决ContentType的问题，将其改为image/jpg
         */
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);
        ossClient.putObject(bucketName, objectName, inputStream, meta);
    }

    /**
     * 文件上传 ，上传在oss中的目录默认使用配置文件中的目录
     * @param objectName 对象名字
     * @param fileBase64 文件base64格式
     * @param bucketName 桶的名字
     * @throws Exception
     */
    public static void upload(String fileBase64,String bucketName,String objectName) throws Exception {
        upload(fileBase64,bucketName,objectName,CONTENT_TYPE);
    }

    /**
     * 文件上传
     * @param fileBase64 文件base64
     * @param bucketName 桶的名字
     * @param objectName 对象名字
     * @return 返回图片的链接，是一个下载链接
     * @throws Exception
     */
    public static void upload(String fileBase64,String bucketName,String objectName,String contentType) throws Exception {
        // 去掉前缀
        String prefix = fileBase64.substring(0,fileBase64.indexOf(",") + 1);
        fileBase64 = fileBase64.replace(prefix,"");
        byte[] decode = Base64.getDecoder().decode(fileBase64.replaceAll("\r\n", ""));
        /*
         * 下面两行代码是重点坑：
         * 现在阿里云OSS 默认图片上传ContentType是image/jpeg
         * 也就是说，获取图片链接后，图片是下载链接，而并非在线浏览链接，
         * 因此，这里在上传的时候要解决ContentType的问题，将其改为image/jpg
         */
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
        ossClient.putObject(bucketName, objectName, byteArrayInputStream,meta);
    }


    /**
     * 下载文件
     * @param objectName  <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
     * @return 返回本地存储地址
     */
    public static String download(String bucketName,String objectName) throws IOException {
        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
        InputStream content = ossObject.getObjectContent();
        if (content != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            content.close();
        }
        return "";
    }

    /**
     * 获取图片链接
     * @param bucketName 桶的名字
     * @param objectName 对象名字
     * @return 图片链接
     */
    public static String getAccessURL(String bucketName, String objectName) {
        Date expiration = new Date(System.currentTimeMillis() + 60 * 1000 * url.getExpiryTime());
        URL url = ossClient.generatePresignedUrl(bucketName,objectName,expiration);
        return url.toString();
    }

    public static InputStream getFile(String bucketName, String objectName) {
        OSSObject object = ossClient.getObject(bucketName, objectName);
        if (object == null) {
            throw new BizException("桶 " + bucketName + " 中不存在 " + objectName + "文件");
        }
        return object.getObjectContent();
    }

    public static int deleteObjects(String bucketName, List<String> groupObjectNames) {
        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(
                new DeleteObjectsRequest(bucketName).withKeys(groupObjectNames).withEncodingType(OSSConstants.URL_ENCODING)
        );
        return deleteObjectsResult.getDeletedObjects().size();
    }

    public static void deleteObject(String bucketName, String objectNames) {
        ossClient.deleteObject(bucketName, objectNames);
    }
}
