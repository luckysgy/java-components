package com.concise.component.core.utils.file;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * 图片处理工具类
 *
 * @author ruoyi
 */
public class ImageUtils {
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] getImage(String imagePath) {
        InputStream is = getFile(imagePath);
        try {
            return IOUtils.toByteArray(is);
        }
        catch (Exception e) {
            log.error("图片加载异常 {}", e.getMessage());
            return null;
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static InputStream getFile(String imagePath) {
        try {
            byte[] result = readFile(imagePath);
            result = Arrays.copyOf(result, result.length);
            return new ByteArrayInputStream(result);
        }
        catch (Exception e) {
            log.error("获取图片异常 {}", e);
        }
        return null;
    }

    /**
     * 读取文件为字节数据
     * 
     * @param key 地址
     * @return 字节数据
     */
    public static byte[] readFile(String url) {
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            // 网络地址
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            urlConnection.setConnectTimeout(30 * 1000);
            urlConnection.setReadTimeout(60 * 1000);
            urlConnection.setDoInput(true);
            in = urlConnection.getInputStream();
            return IOUtils.toByteArray(in);
        }
        catch (Exception e) {
            log.error("访问文件异常 {}", e);
            return null;
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * 将输入流保存成图片
     * @param inputStream 输入流
     * @param filePath 图片路径
     */
    public static void save(InputStream inputStream, String filePath) {
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        File imageFile = new File(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
             ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len = inputStream.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            // 写入数据
            fileOutputStream.write(outStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络图片下载
     * @param imageUrl 图片url
     * @param formatName 文件格式名称
     * @param localFile 下载到本地文件
     * @return 下载是否成功
     */
    public static boolean downloadImage(String imageUrl, String formatName, File localFile) throws IOException {
        URL url;
        try {
            url = new URL(imageUrl);
            return ImageIO.write(ImageIO.read(url), formatName, localFile);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 网络图片下载
     * @param imageUrl 图片url
     * @param formatName 文件格式名称
     * @return 下载的字节
     */
    public static byte[] downloadImage(String imageUrl, String formatName) throws IOException {
        URL url;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            url = new URL(imageUrl);
            ImageIO.write(ImageIO.read(url), formatName, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw e;
        }
    }
}
