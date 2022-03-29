package com.concise.component.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shenguangyang
 * @date 2022-03-29 21:27
 */
class UrlUtilsTest {

    @Test
    void test() {
        System.out.println("removeStartsSlash = " + UrlUtils.removeStartsSlash("/opt/test"));
        System.out.println("removeEndSlash = " + UrlUtils.removeEndSlash("/opt/test/"));
        System.out.println("addStartsSlash = " + UrlUtils.addStartsSlash("/opt/test"));
        System.out.println("addStartsSlash = " + UrlUtils.addStartsSlash("opt/test"));
        System.out.println("addEndSlash = " + UrlUtils.addEndSlash("opt/test/"));
        System.out.println("addEndSlash = " + UrlUtils.addEndSlash("opt/test"));
    }
}