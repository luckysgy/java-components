package com.concise.component.util.file;

import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author shenguangyang
 * @date 2021/7/16 3:39
 */
public class ZipUtils {
    private static final Logger log = LoggerFactory.getLogger(ZipUtils.class);
    /**
     * 将每个文件的的字节数组添加到压缩包中
     * @param inputStreamMap
     * @param zipOutputStream
     */
    public static void zipFile(Map<String,byte[]> inputStreamMap, ZipOutputStream zipOutputStream){
        inputStreamMap.forEach((k,v) ->{
            String fileName = k;
            InputStream is = new ByteArrayInputStream(v);
            ZipEntry zipEntry = new ZipEntry(fileName);
            try {
                try {
                    zipOutputStream.putNextEntry(zipEntry);

                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
                //hutool工具包，直接将输入流刷新到输出流
                IoUtil.copy(is,zipOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(is != null){
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
