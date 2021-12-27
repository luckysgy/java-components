package com.concise.component.datasource.mybatisplus;

import com.concise.component.datasource.mybatisplus.register.EnableMybatisPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * 如果使用了自定义sql 且有字段使用了自动填充,则xml中这些字段不要做空判断
 * https://www.cnblogs.com/siroinfo/p/13095637.html
 * @author shenguangyang
 * @date 2021/6/19 8:19
 */
@EnableMybatisPlus(value = false)
@ComponentScan(basePackages = "com.concise.component.datasource.mybatisplus")
public class DataSourceMybatisPlusMainConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceMybatisPlusMainConfig.class);
    @PostConstruct
    public void init() {
        log.info("init com.concise.component.datasource.mybatisplus");
    }
}
