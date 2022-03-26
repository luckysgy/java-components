package com.concise.component.util.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.concise.component.core.PackageMark;
import com.concise.component.core.utils.OSInfo;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author shenguangyang
 * @date 2021-12-05 0:22
 */
public class JarUtils {
    private static final Logger log = LoggerFactory.getLogger(JarUtils.class);

    /**
     * 获取当前jar包所在路径
     *
     * 如果时在jar包中, 获取到的路径是:
     *  file:/D:/target/demo.jar!/BOOT-INF/lib/lib-common-v20210901.jar!/
     * 如果运行在非jar包中, 获取到的路径是:
     *  /D:/target/mys-project-lib/lib-common/target/classes/
     *
     * 所以要对运行jar包中的路径进行一下处理
     *
     * @return 路径
     */
    public static String getCurrentJarPath() {
        URL location = JarUtils.class.getProtectionDomain().getCodeSource().getLocation();
        String path = location.getPath();
        if (path.startsWith("file:")) {
            String[] split = path.replace("file:", "").split("!/");
            return split[0];
        }
        return path;
    }

    /**
     * 判断是否运行在jar包内
     *
     * @return true 运行在jar包内, false 运行在非jar包内
     */
    public static boolean isRunningInJar() {
        URL url = JarUtils.class.getResource("");
        if (url == null) {
            throw new RuntimeException("JarUtils.class.getResource(\"\") == null ");
        }
        String protocol = url.getProtocol();

        if ("jar".equals(protocol)) {
            // jar 中
            return true;
        } else if ("file".equals(protocol)) {
            // 非jar 中 （文件class 中）
            return false;
        }
        return false;
    }

    /**
     * 拷贝目录
     * @param targetDirPathFromLocal 来自于本地的目标目录路径
     */
    public static void copyDir(String targetDirPathFromLocal, Class<? extends PackageMark> packageMarkClass) {
        try {
            if (!JarUtils.isRunningInJar()) {
                URL url = packageMarkClass.getResource("");
                if (url == null) {
                    throw new RuntimeException("aClass.getResource(\"\") == null");
                }
                String path = url.getPath();
                path = path.replace("/", File.separator);
                if (OSInfo.isWindows()) {
                    path = path.substring(1);
                }
                List<File> allFile = FileUtil.loopFiles(path);

                String packagePath = packageMarkClass.getName().replace(".", File.separator);

                for (File srcFile : allFile) {
                    String filePath = srcFile.getPath();
                    // fileRelativelyPath 是相对路径
                    String fileRelativelyPath = filePath.replace(path, "").replace(packagePath, "");
                    String fileAbsolutePath = targetDirPathFromLocal + File.separator + fileRelativelyPath;
                    String prePath = fileAbsolutePath.substring(0, fileAbsolutePath.lastIndexOf(File.separator));

                    if (StrUtil.isNotEmpty(prePath)) {
                        File preDir = new File(prePath);
                        if (!preDir.exists()) {
                            if (!preDir.mkdirs()) {
                                throw new RuntimeException("create dir fail: dirPath: " + preDir.getPath());
                            }

                        }
                    }
                    File targetFile = new File(fileAbsolutePath);
                    Files.copy(srcFile, targetFile);
                }
            } else {
                System.out.println("运行在非jar中");
                String currentJarPath = JarUtils.getCurrentJarPath();
                JarFile jarFile = new JarFile(currentJarPath);
                Enumeration<JarEntry> entries = jarFile.entries();
                String currentJarPath1 = getCurrentJarPath();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
//                    if (entryName.contains("BOOT-INF/lib/" + jarName)) {
//                        InputStream inputStream = jarFile.getInputStream(entry);
//                        File file = new File(tmpLibJarPath);
//                        writeFile(inputStream, file);
//                        break;
//                    }
                    System.out.println(entryName);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void writeFile(InputStream is, File file) throws Exception{
        if(file != null){
            //推荐使用字节流读取，因为虽然读取的是文件，如果是 .exe, .c 这种文件，用字符流读取会有乱码
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            //这里用小数组读取，使用file.length()来一次性读取可能会出错（亲身试验）
            byte[] bytes = new byte[2048 * 1024];
            int len;
            while((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.close();
        }

    }

    public static void main(String[] args) {
        System.out.println(getCurrentJarPath());
    }
}
