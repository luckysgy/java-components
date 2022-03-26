package com.concise.component.util.file;

import com.concise.component.util.test.data1.DataPackageMark;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shenguangyang
 * @date 2022-03-26 16:28
 */
class JarUtilsTest {

    @Test
    void copyDir() {
        JarUtils.copyDir("/temp/jar-file-data", DataPackageMark.class);
    }
}