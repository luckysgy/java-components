package com.concise.component.core.utils;

import com.concise.component.core.utils.file.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author shenguangyang
 * @date 2022-02-02 10:06
 */
class FileUtilsTest {

    @Test
    void downloadOfText() throws Exception {
        String url = "http://192.168.5.248:9000/demo/test.sh";
        String text = FileUtils.downloadOfText(url, "GET");
        System.out.println(text);
    }

    @Test
    void downloadOfFile() throws IOException {
        String url = "http://192.168.5.248:9000/demo/test.sh";
        FileUtils.downloadOfFile(url, "/temp", "test.sh",  "GET");
    }
}