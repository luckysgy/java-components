package com.concise.component.datasource.mybatisplus.utils;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 * 数据库初始化
 * @author shenguangyang
 */
public class DataBaseInit {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseInit.class);
    private static final String MYSQL8_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL5_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private DataBaseInit()
    {
        super();
    }
    
    /**
     * 使用数据库脚本进行数据初始化-JdbcTemplate实现
     * @see [类、类#方法、类#成员]
     */
    public static void initDb(String jdbcUrl , String driver , String username , String password , String initDb)
            throws ClassNotFoundException {
        // 建库用临时DataSource
        MysqlDataSource dataSource = new MysqlDataSource();
        Class.forName(driver);
        // 去掉jdbcUrl中的数据库
        String notDbJdbcUrl = jdbcUrl.replace("/" + initDb,"");
        logger.info("去掉数据库名后的jdbcUrl [{}]",notDbJdbcUrl);
        dataSource.setUrl(notDbJdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        logger.info("JdbcTemplate = {}", jdbcTemplate);
        // 取数据库名
        logger.info("jdbcUrl = {}, dataBase = {}", jdbcUrl, initDb);
        // 按需建库
        if (MYSQL8_DRIVER_CLASS_NAME.equals(driver)) {
            jdbcTemplate.execute(String.format("CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8mb4", initDb));
        } else if (MYSQL5_DRIVER_CLASS_NAME.equals(driver)) {
            jdbcTemplate.execute(String.format("CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8", initDb));
        }
        logger.info("数据库 [{}] 已被创建",initDb);

    }
}