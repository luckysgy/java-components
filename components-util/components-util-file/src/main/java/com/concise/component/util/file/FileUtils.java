package com.concise.component.util.file;

import com.concise.component.core.exception.UtilException;
import com.concise.component.core.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 文件处理工具类
 * 
 * @author shenguangyang
 */
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    /** 字符常量：斜杠 {@code '/'} */
    public static final char SLASH = '/';

    /** 字符常量：反斜杠 {@code '\\'} */
    public static final char BACKSLASH = '\\';

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 创建文件夹
     * @param dirPath 文件夹目录
     */
    public static void mkdirs(String dirPath) {
        File file = new File(dirPath);
        mkdirs(file);
    }

    /**
     * 创建文件夹
     * @param file 文件
     */
    public static void mkdirs(File file) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new UtilException("dir mkdirs fail: " + file.getPath());
            }
        }
    }

    /**
     * 读取文件全部字节
     * @param path 文件路径
     * @return
     */
    public static byte[] readAllBytes(String path) throws IOException {
        return Files.readAllBytes(new File(path).toPath());
    }

    /**
     * 将Byte数组转换成文件
     * @param bytes 字节数组
     * @param filePath 文件路径
     * @param fileName 文件名
     */
    public static void writeBytesToFile(byte[] bytes, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
            mkdirs(dir);
        }
        File file = new File(filePath + "\\" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);) {
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取路径下的所有文件/文件夹
     * @param directoryPath 需要遍历的文件夹路径
     * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
     * @param excludeFilePathRegs 排除的文件路径正则表达式  eg:  /abc/** ===> /abc/[^/]*
     * @return
     */
    public static List<String> getAllFile(String directoryPath, boolean isAddDirectory, List<String> excludeFilePathRegs) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        assert files != null;
        for (File file : files) {
            String path = file.getPath();
            if (checkExclude(path,excludeFilePathRegs)) {
                continue;
            }
            if (file.isDirectory()) {
                if(isAddDirectory){
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllFile(file.getAbsolutePath(), isAddDirectory, excludeFilePathRegs));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }

    /**
     * 校验是否排除
     * @param targetPath 目标路径  /abc/34/4353
     * @param excludeFilePathRegs 正则表达式集合 比如 /abc/[^/]* , /abc/.* , /[^/]*.do ...
     * @return true 排除  false 不排除
     */
    private static boolean checkExclude(String targetPath, List<String> excludeFilePathRegs) {
        if (excludeFilePathRegs == null || excludeFilePathRegs.size() == 0 ) {
            return false;
        }
        for (String excludeFilePathReg : excludeFilePathRegs) {
            if (Pattern.compile(excludeFilePathReg).matcher(targetPath).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 替换文件中的内容
     * @param oldString 旧内容
     * @param newString 新字符串
     * @param targetPath 目标文件
     */
    public static void replaceContent( String targetPath, String oldString, String newString){
        try {
            File targetFile = new File(targetPath);
            if (targetFile.isDirectory()) {
                return;
            }
            long start = System.currentTimeMillis(); //开始时间
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream( targetPath))); //创建对目标文件读取流
            File newFile = new File("/temp/newFile_" + UUID.randomUUID().toString()); //创建临时文件
            if (!newFile.exists()){
                File file = new File("/temp");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new RuntimeException("临时文件夹创建失败: /temp");
                    }
                }
                //不存在则创建
                if (!newFile.createNewFile()) {
                    throw new RuntimeException("临时文件创建失败: " + newFile.getPath());
                }
            }
            //创建对临时文件输出流，并追加
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(newFile,true)));
            //存储对目标文件读取的内容
            String string = null;
            //替换次数
            int sum = 0;
            while ((string = br.readLine()) != null){
                //判断读取的内容是否包含原字符串
                if (string.contains(oldString)){
                    //替换读取内容中的原字符串为新字符串
                    string = new String(
                            string.replace(oldString,newString));
                    sum++;
                }
                bw.write(string);
                // 添加换行
                bw.newLine();
            }
            // 关闭流，对文件进行删除等操作需先关闭文件流操作
            br.close();
            bw.close();

            String filePath = targetFile.getPath();
            // 删除源文件
            if (!targetFile.delete()) {
                throw new RuntimeException("删除源文件失败: " + targetPath);
            }
            // 将新文件更名为源文件
            if (!newFile.renameTo(new File(filePath))) {
                throw new RuntimeException("文件重命名失败, " + newFile.getPath() + " to " + filePath);
            }
            long time = System.currentTimeMillis() - start; //整个操作所用时间;
            log.info("路径 " + targetPath + " 中有 " + sum +  " 个 " + oldString + " 替换成 " + newString + " 耗费时间: " + time );
        } catch(Exception e){
            log.error(e.getMessage());
        }
    }


    /**
     * 输出指定文件的byte数组
     * 
     * @param filePath 文件路径
     * @param os 输出流
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     * 
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否可下载
     * 
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, "..")) {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource))) {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 下载文件名重新编码
     * 
     * @param request 请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 返回文件名
     *
     * @param filePath 文件
     * @return 文件名
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return SLASH == c || BACKSLASH == c;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }
}
