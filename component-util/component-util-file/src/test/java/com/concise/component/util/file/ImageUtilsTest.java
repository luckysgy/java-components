package com.concise.component.util.file;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shenguangyang
 * @date 2022-02-02 10:05
 */
class ImageUtilsTest {

    @Test
    void downloadImageToLocal() throws IOException {
        String imageUrl = "http://192.168.5.248:9000/demo/2.jpg";
        ImageUtils.downloadImage(imageUrl, "jpg", new File("/temp/1.jpg"));
    }

    @Test
    void downloadImage() throws IOException {
        String imageUrl = "http://192.168.5.248:9000/demo/2.jpg";
        byte[] bytes = ImageUtils.downloadImage(imageUrl, "jpg");
        System.out.println(bytes.length);
    }
}