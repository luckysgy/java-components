package com.concise.component.lib.common.lib;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.concise.component.core.exception.BizException;
import com.concise.component.lib.common.LibConstant;
import com.concise.component.core.utils.OSInfo;
import com.concise.component.core.utils.file.JarUtils;
import com.google.common.io.Files;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author shenguangyang
 * @date 2021-12-05 7:53
 */
public abstract class LibPath {
    private static final Map<String, Object> libExecCache = new ConcurrentHashMap<>();
    private static final String EMPTY = "";

    /**
     * 存放库的包路径
     * 格式为 com/simplifydev/lib/hik/linux64/libhpr.so  或者
     *       com\simplifydev\lib\hik\linux64\hpr.dll
     */
    private static final List<String> libFileNames = new ArrayList<>();

    /**
     * 获取lib后缀
     */
    private static String libSuffix() {
        if (OSInfo.isWindows()) {
            return ".dll";
        } else if (OSInfo.isLinux() || OSInfo.isMacOS()) {
            return ".so";
        }
        return ".dll";
    }

    /**
     * 获取lib前缀
     */
    private static String libPre() {
        if (OSInfo.isWindows()) {
            return "";
        } else if (OSInfo.isLinux() || OSInfo.isMacOS()) {
            return "lib";
        }
        return "";
    }

    protected static synchronized <T extends LibPath> String getPath(LibEnums libEnums, Class<T> libName) {
        try {
            String libPath = copy(LibEnums.HIK) +  File.separator + getLibFileName(libName);
            System.out.println("load lib: " + libPath);
            return URLDecoder.decode(libPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param libPackagePath 库的包路径 eg: com/simplifydev/lib/hik/linux64/libhpr.so
     */
    private static void addLibPackagePath(String libPackagePath) {
        libFileNames.add(libPackagePath);
    }

    private static <T extends LibPath> String getLibFileName(Class<T> libName) {
        String name = libName.getSimpleName().toLowerCase();
        for (String libFileName : libFileNames) {
            String targetName = libPre() + name + libSuffix();
            if (libFileName.toLowerCase().contains(targetName)) {
                return libFileName;
            }
        }
        libFileNames.clear();
        return "error";
    }

    /**
     * 执行解析
     * 返回库路径的根路径
     */
    private static synchronized String copy(LibEnums libEnums) throws Exception {
        String packageMark = "";
        String packageName = "";
        String jarName = "";
        String packageRootPath = "";

        if (OSInfo.isLinux()) {
            packageMark = libEnums.getLinux64PackageMark();
            jarName = libEnums.getLinux64JarName();
        } else if (OSInfo.isWindows()) {
            packageMark = libEnums.getWin64PackageMark();
            jarName = libEnums.getWin64JarName();
        } else {
            throw new BizException("不支持的操作系统类型: " + OSInfo.getOSName());
        }

        packageName = packageMark.substring(0, packageMark.lastIndexOf("."));
        packageRootPath = packageName.replace(".", File.separator);
        String libRootPath = LibConstant.LIB_CACHE_PATH + File.separator + jarName.replace(".jar", "") + File.separator + packageRootPath;

        Object value = libExecCache.get(jarName);
        // != null 说明已经执行过了, 直接返回
        if (value != null) {
            return libRootPath;
        }

        if (!JarUtils.isRunningInJar()) {
            Class<?> aClass = Class.forName(packageMark);
            URL url = aClass.getResource("");
            if (url == null) {
                throw new RuntimeException("aClass.getResource(\"\") == null");
            }
            String path = url.getPath();
            path = path.replace("/", File.separator);
            if (OSInfo.isWindows()) {
                path = path.substring(1);
            }
            List<File> allFile = FileUtil.loopFiles(path);

            for (File srcFile : allFile) {
                String filePath = srcFile.getPath();
                // libPath是相对路径
                String libPath = filePath.replace(path, "").replace(packageRootPath, "");
                String libAbsolutePath = libRootPath + File.separator + libPath;
                String prePath = libAbsolutePath.substring(0, libAbsolutePath.lastIndexOf(File.separator));

                if (StrUtil.isNotEmpty(prePath)) {
                    File preDir = new File(prePath);
                    if (!preDir.exists()) {
                        if (!preDir.mkdirs()) {
                            throw new RuntimeException("create dir fail: dirPath: " + preDir.getPath());
                        }
                    }
                }

                addLibPackagePath(libPath);
                File targetFile = new File(libAbsolutePath);
                Files.copy(srcFile, targetFile);
            }
        } else {
            String tmpLibJarPath = LibConstant.LIB_CACHE_PATH + File.separator + jarName;
            String loaderPath = System.getProperty("loader.path");
            if (loaderPath == null) {
                String currentJarPath = JarUtils.getCurrentJarPath();
                JarFile jarFile = new JarFile(currentJarPath);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.contains("BOOT-INF/lib/" + jarName)) {
                        InputStream inputStream = jarFile.getInputStream(entry);
                        File file = new File(tmpLibJarPath);
                        writeFile(inputStream, file);
                        parseLibJarFile(tmpLibJarPath, packageRootPath, libRootPath);
                        break;
                    }
                }
            } else {
                // 用户将所有lib单独提出来了
                loaderPath = loaderPath.replace("\\", File.separator);
                File fromFile = new File(loaderPath + File.separator + jarName);
                File toFile = new File(tmpLibJarPath);
                Files.copy(fromFile, toFile);
                parseLibJarFile(tmpLibJarPath, packageRootPath, libRootPath);
            }

        }
        libExecCache.put(jarName, EMPTY);
        return libRootPath;
    }


    /**
     * 解析lib的jar包中的所有文件
     * @param tmpLibJarPath 存放lib的jar包文件路径
     * @param libRootPath 存放lib文件的根路径
     * @param packageRootPath 包根路径
     */
    private static void parseLibJarFile(String tmpLibJarPath, String packageRootPath, String libRootPath) throws Exception {
        JarFile targetJarFile = new JarFile(tmpLibJarPath);
        Enumeration<JarEntry> targetEntries = targetJarFile.entries();
        while (targetEntries.hasMoreElements()) {
            JarEntry targetEntry = targetEntries.nextElement();
            String targetEntryName = targetEntry.getName().replace("/", File.separator);
            String libPath = targetEntryName.replace(packageRootPath + File.separator, "");
            String libAbsolutePath = libRootPath + File.separator + libPath;

            // 排除 非文件包 等路径
            if (!targetEntryName.contains(packageRootPath) ||
                    (targetEntryName.lastIndexOf(File.separator) == (targetEntryName.length() -1))) {
                continue;
            }

            // 第三段目的为了解析jar包中的文件路径
            String targetFileDir = libAbsolutePath.substring(0, libAbsolutePath.lastIndexOf(File.separator));
            File targetDir = new File(targetFileDir);
            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) {
                    throw new RuntimeException("create dir fail for target libDir " + targetDir.getPath());
                }
            }
            addLibPackagePath(libPath);

            InputStream srcInputStream = targetJarFile.getInputStream(targetEntry);
            File targetFile =  new File(libAbsolutePath);
            writeFile(srcInputStream, targetFile);
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
}
