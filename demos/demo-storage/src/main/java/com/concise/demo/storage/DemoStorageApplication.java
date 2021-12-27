package com.concise.demo.storage;

import com.concise.component.datasource.mybatisplus.register.EnableMybatisPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shenguangyang
 * @date 2021-12-25 21:01
 */
//@EnableMybatisPlus
@SpringBootApplication
public class DemoStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoStorageApplication.class, args);
    }
}
