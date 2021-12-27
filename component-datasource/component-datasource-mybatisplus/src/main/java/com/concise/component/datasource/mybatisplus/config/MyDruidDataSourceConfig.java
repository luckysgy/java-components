package com.concise.component.datasource.mybatisplus.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.concise.component.datasource.mybatisplus.utils.DataBaseInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * 重写连接池配置
 * @author shenguangyang
 */
@Configuration
@ConditionalOnClass({DruidDataSource.class})
public class MyDruidDataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(MyDruidDataSourceConfig.class);

    @Value("${spring.datasource.init-db:default}")
    private String initDb;
    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void init() {
        logger.info("初始化数据库 initDb [{}] driverClassName [{}] jdbcUrl [{}] username [{}] password [{}]",
        initDb,driverClassName,jdbcUrl,username,password);
        try {
            DataBaseInit.initDb(jdbcUrl,driverClassName,username,password,initDb);
        } catch (Exception e) {
            logger.error("数据库创建失败 errorMessage [{}]",e.getMessage());
        }
    }
    public MyDruidDataSourceConfig() {

    }
}