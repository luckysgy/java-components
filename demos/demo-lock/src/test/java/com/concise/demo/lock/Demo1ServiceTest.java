package com.concise.demo.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shenguangyang
 * @date 2021-12-26 9:19
 */
@SpringBootTest
class Demo1ServiceTest {
    @Autowired
    private Demo1Service demo1Service;
    @Test
    void test1() {
        demo1Service.test();
    }
}