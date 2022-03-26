package com.concise.component.util.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.concise.component.core.PackageMark;
import com.concise.component.core.utils.OSInfo;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.core.utils.UrlUtils;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
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
     * 从jar包中的classes拷贝数据
     * @param targetDirPathFromLocal 来自于本地的目标目录路径
     * @param packageMarkClass 包标记类, PackageMark子类所在的包中数据将被拷贝到指定目录, 不拷贝class文件
     */
    public static void copyDataFromClasses(String targetDirPathFromLocal, Class<? extends PackageMark> packageMarkClass) {
        copyDir(true, "", targetDirPathFromLocal, packageMarkClass);
    }

    /**
     * 从jar包中的lib中拷贝数据
     * @param libName jar包中的lib名称, eg： component-util-file-1.0.0.jar
     * @param targetDirPathFromLocal 来自于本地的目标目录路径
     * @param packageMarkClass 包标记类, PackageMark子类所在的包中数据将被拷贝到指定目录, 不拷贝class文件
     */
    public static void copyDataFromLib(String libName, String targetDirPathFromLocal, Class<? extends PackageMark> packageMarkClass) {
        copyDir(false, libName, targetDirPathFromLocal, packageMarkClass);
    }


    /**
     * 拷贝目录
     *
     * maven需要配置资源, 具体配置方式请看 pom.xml
     * @param isCurrentApp 是否拷贝调用方所在模块中的数据文件夹
     * @param libName 当isCurrentApp为false时候, 需要传入依赖的lib名称 eg： component-util-file-1.0.0.jar
     * @param targetDirPathFromLocal 来自于本地的目标目录路径
     */
    private static void copyDir(boolean isCurrentApp, String libName, String targetDirPathFromLocal, Class<? extends PackageMark> packageMarkClass) {
        try {
            String currentJarPath = JarUtils.getCurrentJarPath();
            targetDirPathFromLocal = FileUtils.winToLinuxForPath(targetDirPathFromLocal);
            targetDirPathFromLocal = UrlUtils.addLastSlash(targetDirPathFromLocal);

            String packagePath = packageMarkClass.getName().replace(".", "/")
                    .replace(packageMarkClass.getSimpleName(), "");
            if (!JarUtils.isRunningInJar()) {
                URL url = packageMarkClass.getResource("");
                if (url == null) {
                    throw new RuntimeException("aClass.getResource(\"\") == null");
                }
                String path = url.getPath();
                if (OSInfo.isWindows()) {
                    path = path.substring(1);
                }
                path = FileUtils.winToLinuxForPath(path);
                List<File> allFile = FileUtil.loopFiles(path);

                for (File srcFile : allFile) {
                    String filePath = srcFile.getPath();
                    filePath = FileUtils.winToLinuxForPath(filePath);
                    // 排除class文件
                    if (filePath.endsWith(".class")) {
                        continue;
                    }
                    // fileRelativelyPath 是相对路径
                    String targetFileRelativelyPath = filePath.replace(path, "").replace(packagePath, "");
                    String targetFileAbsolutePath = targetDirPathFromLocal + targetFileRelativelyPath;
                    String targetDirPath = targetFileAbsolutePath.substring(0, targetFileAbsolutePath.lastIndexOf("/"));

                    if (StrUtil.isNotEmpty(targetDirPath)) {
                        File preDir = new File(targetDirPath);
                        if (!preDir.exists()) {
                            if (!preDir.mkdirs()) {
                                throw new RuntimeException("create dir fail: dirPath: " + preDir.getPath());
                            }

                        }
                    }
                    File targetFile = new File(targetFileAbsolutePath);
                    Files.copy(srcFile, targetFile);
                }
            } else {
                if (isCurrentApp) {
                    copyClassesToLocal(currentJarPath, packagePath, targetDirPathFromLocal);
                } else {
                    String libPath = copyLibToLocal(currentJarPath, libName, targetDirPathFromLocal);
                    if (StringUtils.isEmpty(libPath)) {
                        log.warn("copyLibInJarToLocal return path is empty");
                        return;
                    }
                    copyClassesToLocal(libPath, packagePath, targetDirPathFromLocal);
                    FileUtils.deleteFile(libPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝jar包中某个lib
     * @param jarPath jar包路径
     * @param libName jar包中的lib名称, eg: component-util-file-1.0.0.jar
     * @param targetDirPathFromLocal 拷贝到的本地目录
     * @return 路径
     */
    private static String copyLibToLocal(String jarPath, String libName, String targetDirPathFromLocal) throws Exception {
        targetDirPathFromLocal = UrlUtils.addLastSlash(targetDirPathFromLocal);
        String targetLibPath = targetDirPathFromLocal + System.currentTimeMillis() + "-" + libName;
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                // 拷贝其他模块中的数据文件
                if (entryName.contains("BOOT-INF/lib/" + libName)) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    File targetLibFile = new File(targetLibPath);
                    FileUtils.writeFile(inputStream, targetLibFile);
                    return targetLibPath;
                }
            }
        }

        return "";
    }
    /**
     * 拷贝jar包中某个目录到本地, Classes表示的是jar包中 src/java下的所有文件
     * @param jarPath jar包路径
     * @param packagePath 拷贝的目录所在包路径
     * @param targetDirPathFromLocal 拷贝到本地的目标路径
     */
    private static void copyClassesToLocal(String jarPath, String packagePath, String targetDirPathFromLocal) throws Exception {
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                String targetFileRelativelyPath = entryName.replace("BOOT-INF/classes/", "")
                        .replace(packagePath, "");
                String targetFileAbsolutePath = targetDirPathFromLocal + targetFileRelativelyPath;
                String targetDirPath = targetFileAbsolutePath.substring(0, targetFileAbsolutePath.lastIndexOf("/"));
                // entryName.contains("BOOT-INF/classes/" + packagePath
                if (entryName.contains(packagePath) && entry.getSize() > 0) {
                    if (entryName.endsWith(".class")) {
                        continue;
                    }
                    File preDirFile = new File(targetDirPath);
                    if (!preDirFile.exists()) {
                        FileUtils.mkdirs(preDirFile);
                        log.debug("create dir: {}", targetDirPath);
                    }

                    File file = new File(targetFileAbsolutePath);
                    InputStream inputStream = jarFile.getInputStream(entry);
                    log.debug("targetFileAbsolutePath: {}, entrySize: {}", targetFileAbsolutePath, entry.getSize());
                    FileUtils.writeFile(inputStream, file);
                }
                //System.out.println(entryName);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getCurrentJarPath());
    }
}
