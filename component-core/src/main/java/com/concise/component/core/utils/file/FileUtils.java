package com.concise.component.core.utils.file;

import cn.hutool.core.util.ObjectUtil;
import com.concise.component.core.exception.UtilException;
import com.concise.component.core.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
     * 将win路径转成linux路径格式
     * eg: E:\mnt\test ===> /mnt/test
     * @param winPath win路径
     * @return linux路径格式
     */
    public static String winToLinuxForPath(String winPath) {
        if (winPath.contains(":")) {
            return winPath.substring(2).replace("\\", "/");
        } else {
            return winPath.replace("\\", "/");
        }
    }

    /**
     * 创建文件
     * @param path  全路径 指向文件
     */
    public static boolean makeFile(String path) {
        File file = new File(path);
        if(file.exists()) {
            return false;
        }
        if (path.endsWith(File.separator)) {
            return false;
        }
        if(!file.getParentFile().exists()) {
            if(!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 输入流写入文件
     *
     * @param is
     *            输入流
     * @param file
     *            文件
     */
    public static void writeFile(InputStream is, File file) throws Exception{
        OutputStream os = null;
        FileOutputStream fos = null;
        try {
            if (file == null) {
                return;
            }
            fos = new FileOutputStream(file);
            //推荐使用字节流读取，因为虽然读取的是文件，如果是 .exe, .c 这种文件，用字符流读取会有乱码
            os = new BufferedOutputStream(fos);
            //这里用小数组读取，使用file.length()来一次性读取可能会出错（亲身试验）
            byte[] bytes = new byte[2048 * 1024];
            int len;
            while((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
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
            bos.flush();
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
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            if (!file.delete()) {
                log.warn("delete file [{}] fail", filePath);
                return false;
            }
            return true;
        }
        return true;
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

    /**
     * 下载文本
     */
    public static String downloadOfText(String url, String method) throws Exception {
        byte[] bytes = downloadOfBytes(url, method);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 通过url下载文件
     */
    public static byte[] downloadOfBytes(String url, String method) throws Exception {
        InputStream inputStream = null;
        HttpURLConnection conn = null;
        try {
            // 建立链接
            URL httpUrl=new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
            //以Post方式提交表单，默认get方式
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            // 连接指定的资源
            conn.connect();
            // 获取网络输入流
            inputStream = conn.getInputStream();
            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("抛出异常！！");
        } finally {
            if (ObjectUtil.isNotNull(inputStream)) {
                inputStream.close();
            }
            if (ObjectUtil.isNotNull(conn)) {
                conn.disconnect();
            }
        }
        return null;

    }

    /**
     * 下载文件
     * @apiNote 注意文件保存路径的后面一定要加上文件的名称
     * @param url
     * @param filePath
     * @param method
     * @return
     * @throws IOException
     */
    public static File downloadOfFile(String url, String filePath, String fileName, String method) throws IOException {
        //创建不同的文件夹目录
        File folder = new File(filePath);
        //判断文件夹是否存在
        if (!folder.exists()) {
            //如果文件夹不存在，则创建新的的文件夹
            mkdirs(folder);
        }

        //判断文件的保存路径后面是否以/结尾
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        File file = new File(filePath + fileName);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                log.error("file [{}] create fail", filePath + fileName);
            }
        }

        FileOutputStream fileOut = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            // 建立链接
            URL httpUrl=new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
            //以Post方式提交表单，默认get方式
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            //获取网络输入流
            inputStream = conn.getInputStream();
            bis = new BufferedInputStream(inputStream);

            fileOut = new FileOutputStream(filePath + fileName);
            bos = new BufferedOutputStream(fileOut);

            byte[] buf = new byte[4096];
            int length = bis.read(buf);
            //保存文件
            while(length != -1) {
                bos.write(buf, 0, length);

                length = bis.read(buf);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(bos)) {
                bos.close();
            }

            if (ObjectUtil.isNotNull(bis)) {
                bis.close();
            }

            if (ObjectUtil.isNotNull(conn)) {
                conn.disconnect();
            }

            if (ObjectUtil.isNotNull(fileOut)) {
                fileOut.close();
            }

            if (ObjectUtil.isNotNull(inputStream)) {
                inputStream.close();
            }
        }

        return file;

    }
}
