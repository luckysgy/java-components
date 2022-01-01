package com.concise.component.lib.common;

import java.io.File;
import java.io.IOException;

/**
 * @author shenguangyang
 * @date 2021-12-05 7:25
 */
public class LibConstant {
    /**
     * 库缓存的路径
     */
    private static final String LIB_CACHE_PATH_PRE = ".lib" + File.separator + "cache";
    public static String LIB_CACHE_PATH = "";

    static {
        try {
            File cacheDirectory = createCacheDirectory();
            LIB_CACHE_PATH = cacheDirectory.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File createCacheDirectory() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        if (!tempDir.endsWith(File.separator)) {
            tempDir = tempDir + File.separator;
        }
        File generatedDir = new File(tempDir + LibConstant.LIB_CACHE_PATH_PRE);
        if (generatedDir.exists()) {
            return generatedDir;
        }
        if (!generatedDir.mkdirs())
            throw new IOException("Failed to create cache directory " + generatedDir.getPath());

        return generatedDir;
    }
}
