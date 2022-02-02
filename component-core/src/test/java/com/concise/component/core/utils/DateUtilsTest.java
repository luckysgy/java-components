package com.concise.component.core.utils;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

/**
 * @author shenguangyang
 * @date 2022-02-02 10:10
 */
class DateUtilsTest {

    @Test
    void getStartTimeAndEndTimeOfToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(DateUtils.getStartTimeOfToday()));
        System.out.println(sdf.format(DateUtils.getEndTimeOfToday()));
    }
}