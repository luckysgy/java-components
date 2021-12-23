package com.concise.demo.opencv;

import com.concise.component.util.file.FileUtils;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2021-12-23 7:46
 */
public class OpencvDemo {
    @Test
    public void matchImage() throws IOException {
        OpencvMatch opencvMatch = new OpencvMatch();
        List<String> allFile = FileUtils.getAllFile("/mnt/opencv", false, null);
        for (String path : allFile) {
            System.out.println("path: " + path);
            opencvMatch.matchImage(ImageIO.read(new File(path)),
                    ImageIO.read(new File("/mnt/opencv/self-2.jpg")));
            System.out.println("\n\n");
        }

    }
}
