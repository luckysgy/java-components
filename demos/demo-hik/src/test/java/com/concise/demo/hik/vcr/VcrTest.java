package com.concise.demo.hik.vcr;

import com.concise.component.core.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shenguangyang
 * @date 2021-12-26 10:27
 */
@SpringBootTest
class VcrTest {
    @Autowired
    private Vcr vcr;

    @Test
    void test() throws ParseException {
        VcrPlayInfo vcrPlayInfo = new VcrPlayInfo(UUIDUtil.uuid(), 23, "2021-12-24 10:00:00", "2021-12-24 10:10:00");
        VcrLoginInfo vcrLoginInfo = new VcrLoginInfo("192.168.190.70", 28003, "admin", "jsshzl008");
        vcr.login(vcrLoginInfo);
    }
}