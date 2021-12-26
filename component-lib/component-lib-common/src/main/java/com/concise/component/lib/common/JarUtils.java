package com.concise.component.lib.common;

import java.net.URL;

/**
 * @author shenguangyang
 * @date 2021-12-05 0:22
 */
public class JarUtils {
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

    public static void main(String[] args) {
        System.out.println(getCurrentJarPath());
    }
}
