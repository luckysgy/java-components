package com.concise.component.storage.minio.utils;

import com.concise.component.storage.common.autoconfig.StorageProperties;
import com.concise.component.storage.common.partupload.MultiPartUploadInit;
import com.concise.component.storage.minio.client.CustomMinioClient;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import io.minio.messages.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * minio工具类
 * 对象名: xxx/zzz/yyy/fileName.后缀 ==> xxx/zzz/yyy是目录名
 * @author shenguangyang
 */
public class MinioUtils {
    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(MinioUtils.class);

    /**
     * url过期时间
     */
    private static StorageProperties.Minio minioProperties;
    private static StorageProperties.Url urlProperties;


    /**
     * 如果为 application/octet-stream 获取的图片链接只可以下载的
     * image/png：表示可以在线预览
     */
    private static final String CONTENT_TYPE = "application/octet-stream";
    /**
     * minioClient
     */
    private static CustomMinioClient minioClient;

    public static void init(CustomMinioClient customMinioClient, StorageProperties.Minio minioProperties, StorageProperties.Url urlProperties) {
        MinioUtils.minioClient = customMinioClient;
        MinioUtils.minioProperties = minioProperties;
        MinioUtils.urlProperties = urlProperties;
    }

    /**
     * 创建桶
     * @param bucketName 桶的名字
     * @throws Exception
     */
    public static void createBucket(String bucketName) throws Exception{
        if (bucketExists(bucketName)) {
            return;
        }
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 分片上传
     * 需要重写minio客户端，{@link CustomMinioClient}
     * @apiNote 注意一个分片小于5M，会报错，提示
     *          Your proposed upload is smaller than the minimum allowed object size.
     */
    public static class MultiPartUpload {
        /**
         * @param bucketName 桶的名字 如果桶的名字为null或者为空字符串，则使用默认的桶
         * @param objectName 对象名
         * @param totalPart 一共分片数
         * @return 返回到的结果有两个字段
         *          uploadId:上传的id
         *          uploadUrls:每个分片上传url的集合，只能通过put进行上传
         * 响应结果:
         * {
         *     "uploadId": "b7dd9a60-7c11-43f1-acee-bffd4ef2fccb",
         *     "uploadUrls": [
         *         "https://play.minio.io:9000/tuinetest/test/b.jpg?uploadId=b7dd9a60-7c11-43f1-acee-bffd4ef2fccb&partNumber=1&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=Q3AM3UQ867SPQQA43P2F%2F20210324%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20210324T032112Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=e39c8e8c165add0daa50d2da44e51ca752b9213e497633bcfb3431b60383b5be",
         *         "https://play.minio.io:9000/tuinetest/test/b.jpg?uploadId=b7dd9a60-7c11-43f1-acee-bffd4ef2fccb&partNumber=2&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=Q3AM3UQ867SPQQA43P2F%2F20210324%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20210324T032112Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=99611a212d6b791a24df295cb3475a79780ed4c6314ee9ddb8df4179326b7723"
         *     ]
         * }
         * eg: https://github.com/tuine/minio-multipart-upload
         */
        public static MultiPartUploadInit init(String bucketName, String objectName, int totalPart) {
            MultiPartUploadInit result = new MultiPartUploadInit();
            try {
                String uploadId = minioClient.initMultiPartUpload(bucketName, null, objectName, null, null);

                result.setUploadId(uploadId);

                Map<String, String> uploadUrls =  new HashMap<>(32);

                Map<String, String> reqParams = new HashMap<>();
                //reqParams.put("response-content-type", "application/json");
                reqParams.put("uploadId", uploadId);
                for (int i = 1; i <= totalPart; i++) {
                    reqParams.put("partNumber", String.valueOf(i));
                    String uploadUrl = minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.PUT)
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .expiry(1, TimeUnit.DAYS)
                                    .extraQueryParams(reqParams)
                                    .build());
                    uploadUrls.put(String.valueOf(i),uploadUrl);
                }
                result.setUploadUrls(uploadUrls);
            } catch (Exception e) {
                logger.error("error: {}", e.getMessage(), e);
                return result;
            }
            return result;
        }

        /**
         * 合并所有分片
         * @param objectName 对象名
         * @param bucketName 桶的名字
         * @param uploadId 上传文件的eid
         * @return false合并失败，true合并成功
         */
        public static boolean merge(String bucketName, String objectName, String uploadId) {
            try {
                Part[] parts = new Part[1000];
                //此方法注意2020.02.04之前的minio服务端有bug
                ListPartsResponse partResult = minioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
                int partNumber = 1;
                for (Part part : partResult.result().partList()) {
                    parts[partNumber - 1] = new Part(partNumber, part.etag());
                    partNumber++;
                }
                minioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
            } catch (Exception e) {
                logger.error("error: {}", e.getMessage(), e);
                return false;
            }
            return true;
        }
    }


    /**
     * 上传文件
     *
     * @param file
     * @param bucketName 桶的名字
     * @param objectName 对象名 比如 xxx/zzz/fileName.jpg
     * @return 返回的是对象名
     */
    public static void uploadFile(String bucketName, MultipartFile file, String objectName) {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes())) {
            // 文件名 file.getOriginalFilename();
            uploadFile(bucketName,bais,file.getContentType(),objectName);
        } catch (Exception e) {
            logger.error("{}文件上传失败", file.getOriginalFilename());
            throw new RuntimeException("文件上传失败," + file.getOriginalFilename());
        }
    }

    /**
     * 上传文件
     * @param fileBase64 文件base64
     * @param objectName 对象名
     */
    public static void uploadFile(String bucketName,String fileBase64,String objectName) throws Exception {
        uploadFile(bucketName,fileBase64,CONTENT_TYPE,objectName);
    }

    /**
     * 判断桶是否存在
     * @param bucketName
     * @return
     */
    public static boolean bucketExists(String bucketName) {
        // 判断Bucket是否存在
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * 上传文件
     * @param fileBase64 文件base64
     * @param contentType
     * @param objectName 对象名  比如: xxx/zzz/yyy/fileName.jpg
     * @return 返回的是对象名字
     */
    public static void uploadFile(String bucketName,String fileBase64,String contentType,String objectName) throws Exception {
        // 去掉前缀
        String prefix = fileBase64.substring(0,fileBase64.indexOf(",") + 1);
        fileBase64 = fileBase64.replace(prefix,"");
        InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(fileBase64.replaceAll("\r\n","")));
        uploadFile(bucketName,inputStream,contentType,objectName);
    }

    /**
     * 上传文件
     * @param bucketName 桶的名字，如果桶名为null，则使用默认的桶
     * @param stream 流
     * @param contentType
     * @param objectName 对象名  比如: xxx/zzz/yyy/fileName.jpg
     */
    public static void uploadFile(String bucketName,InputStream stream,String contentType,String objectName) throws Exception {
        try {
            // 上传文件
            PutObjectArgs build = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream,-1,ObjectWriteArgs.MAX_PART_SIZE)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(build);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}文件上传失败", objectName);
        } finally {
            stream.close();
        }
    }

    /**
     * 获取文件
     *
     * @param objectName
     * @return java.io.InputStream
     */
    public static InputStream getFile(String bucketName, String objectName) {
        try {
            logger.debug("bucketName = {}, objectName = {}", bucketName, objectName);
            // 文件是否存在
            StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
            minioClient.statObject(statObjectArgs);
            // 获取文件
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}文件获取失败", objectName);
            return null;
        }
    }

    /**
     * 直接下载文件
     *
     * @param req
     * @param res
     * @param fid
     * @param fileName
     * @return void
     */
    public static void lookUploadFile(String bucketName , HttpServletRequest req, HttpServletResponse res, String fid, String fileName) {

        try (InputStream in = getFile(bucketName,fid);
             OutputStream output = res.getOutputStream()) {
            // 得到输入流
            if (in == null) {
                try (PrintWriter printWriter = res.getWriter()) {
                    printWriter.append("404 - File Not Exist");
                } catch (IOException e) {
                    logger.error("数据异常: {}", e);
                }
                return;
            }
            res.reset();
            // res.setContentType(getMimeType(fileName));
            // https://gitee.com/dolyw/codes/2h1r6avwxumegjs89ztyn86
            res.addHeader("content-Disposition", "inline;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            byte[] b = new byte[4096];
            int i = 0;
            while ((i = in.read(b)) > 0) {
                output.write(b, 0, i);
            }
        } catch (MalformedURLException me) {
            logger.error("数据异常: {}", me);
        } catch (IOException e) {
            logger.error("数据异常: {}", e);
        }
    }

    /**
     * 获取外链
     *
     * @param objectName 对象名 格式为 xxx/zzz/yyy/fileName.后缀
     * @return java.lang.String
     * @throws
     */
    public static String getFileUrl(String bucketName , String objectName , Method method) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(method)
                    .bucket(bucketName)
                    .expiry(urlProperties.getExpiryTime(), TimeUnit.MINUTES)
                    .object(objectName)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            logger.error("{}文件获取失败", objectName);
            return "";
        }
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     */
    public static boolean removeBucket(String bucketName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder().bucket(bucketName).build();
            minioClient.removeBucket(removeBucketArgs);
            flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            return !flag;

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     */
    public static Iterable<Result<Item>> listObjects(String bucketName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .recursive(true)
                    .bucket(bucketName).build();
            return minioClient.listObjects(listObjectsArgs);
        }
        return null;
    }


    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    public static InputStream getObject(String bucketName, String objectName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                GetObjectArgs getObjectArgs = GetObjectArgs.builder().object(objectName)
                        .bucket(bucketName).build();
                InputStream inputStream = minioClient.getObject(getObjectArgs);
                return inputStream;
            }
        }
        return null;
    }

    /**
     * 获取对象，存到本地
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName 文件名路径，需要带有后缀 E:\\temp.png
     * @return
     */
    public static boolean getObject(String bucketName, String objectName,String fileName) throws Exception {
        InputStream is = null;
        OutputStream os = null;

        try {
            File file = new File(fileName);
            // 文件存在就删除，重新创建,防止上次的数据影响本次
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            is = getObject(bucketName, objectName);
            os = new FileOutputStream(file);
            int len=-1;
            byte[] bytes = new byte[4096];
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes,0,len);
            }
            return true;
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * 下载对象，断点续传，要求下载的文件大小不能比目标的大，否则会下载失败
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName 文件名称
     * @return
     */
    public static boolean downloadObject(String bucketName, String objectName , String fileName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                DownloadObjectArgs downloadObjectArgs = DownloadObjectArgs.builder().object(objectName)
                        .bucket(bucketName).filename(fileName).build();
                minioClient.downloadObject(downloadObjectArgs);
                return true;
            }
        }
        return false;
    }


    /**
     * 获取对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    public static StatObjectResponse statObject(String bucketName, String objectName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
           StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                                                .object(objectName).bucket(bucketName).build();
            StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs);
            return statObjectResponse;
        }
        return null;
    }


    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     */
    public static boolean removeObject(String bucketName, String objectName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            RemoveObjectArgs removeObject = RemoveObjectArgs.builder()
                    .bucket(bucketName).object(objectName).build();
            minioClient.removeObject(removeObject);
            return true;
        }
        return false;
    }

}