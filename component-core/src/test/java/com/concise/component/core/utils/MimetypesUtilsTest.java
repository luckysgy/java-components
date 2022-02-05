package com.concise.component.core.utils;

import org.junit.jupiter.api.Test;

/**
 * @author shenguangyang
 * @date 2022-02-04 20:15
 */
class MimetypesUtilsTest {

    @Test
    void getContentType() {
        System.out.println(MimetypesUtils.getInstance().getMimetype("about.c4e50a4d.css"));
    }
}