package com.concise.component.datasource.mybatisplus.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : shenguangyang
 * @date : 2021-07-04
 */
public class JdbcOpsUtil {

    private static String jdbcDriver = "jdbc:mysql://localhost:30306/simplifydev?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&serverTimezone=GMT%2B8";
    private static String userName = "root";
    private static String password = "mysql12345678";

    /**
     * 批量插入
     * @param sql sql语句 eg: insert into user values(?,?)
     * @param params 参数 List<String> 存放一条sql需要的参数 ; List<List<String>> 存放全部sql需要的
     */
    public static void batchInsert(String sql, List<List<String>> params) {
        long start = System.currentTimeMillis();
        //1.导入驱动jar包
        //2.注册驱动(mysql5之后的驱动jar包可以省略注册驱动的步骤)
        //Class.forName("com.mysql.jdbc.Driver");
        //3.获取数据库连接对象
        Connection conn = null;
        PreparedStatement pstmt = null;
        {
            try {
                //"&rewriteBatchedStatements=true",一次插入多条数据，只插入一次
                conn = DriverManager.getConnection(jdbcDriver,userName,password);
                //4.定义sql语句, 有参数传入
                //5.获取执行sql的对象PreparedStatement
                pstmt = conn.prepareStatement(sql);
                //6.不断产生sql
                for (List<String> param : params) {
                    if (param.size() == 0) {
                        continue;
                    }
                    for (int i = 1; i <= param.size(); i++) {
                        pstmt.setString(i,param.get(i-1));
                    }
                    pstmt.addBatch();
                }
                //7.往数据库插入一次数据
                pstmt.executeBatch();
                System.out.println("成功插入 " + params.size() + " 条信息！");
                long end = System.currentTimeMillis();
                System.out.println("耗时：" + (end - start)/1000 + "秒");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //8.释放资源
                //避免空指针异常
                if(pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}