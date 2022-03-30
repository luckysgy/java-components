package com.concise.component.core.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author shenguangyang
 * @date 2022-03-30 19:57
 */
class MD5UtilsTest {

    @Test
    void getFileMD5() throws Exception {
        for (int i = 0; i < 100; i++) {
            //此处我测试的是我本机jdk源码文件的MD5值
            String filePath = "D:\\temp\\2.jpg";
            long start = System.currentTimeMillis();
            String md5Hashcode2 = MD5Utils.getFileMD5(new File(filePath));

            System.out.println("MD5Util2计算文件md5值为：" + md5Hashcode2);
            System.out.println("MD5Util2计算文件md5值的长度为：" + md5Hashcode2.length());
            System.out.println("MD5Util2计算文件md5值耗时: " + (System.currentTimeMillis() - start) + " ms");
        }
        TimeUnit.SECONDS.sleep(60);
    }
}