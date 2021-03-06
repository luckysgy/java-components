# 功能

- 整合mybatis plus
- 整合分页插件
- 整合druid
- 可以控制是否使能mybatisplus/druid

# 开启mybatisplus

默认情况下, 即使引入`component-datasource-mybatisplus` 组件，也不会使能mybatiplus

需要在启动类上添加如下注解

```java
// value = false, 关闭mybatisplus, 默认value = true
// 不会使能mybatisplus情况有两种: 启动类没有添加 @EnableMybatisPlus 注解 或者 @EnableMybatisPlus(value = false)
@EnableMybatisPlus
@SpringBootApplication
public class DemoStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoStorageApplication.class, args);
    }
}
```



# yaml配置文件

由于内部重写了数据源类, 需要在配置文件中添加如下配置强制覆盖已有的DataSource

```yaml
spring:
  main:
    allow-bean-definition-overriding: true
```



数据库配置如下:

```xml
spring:
  flyway:
    # 启用或禁用 flyway
    enabled: false
    # flyway 的 clean 命令会删除指定 schema 下的所有 table, 生产务必禁掉。这个默认值是 false 理论上作为默认配置是不科学的。
    clean-disabled: true
    # SQL 脚本的目录,多个路径使用逗号分隔 默认值 classpath:db/migration
    locations: classpath:db/migration
    #  metadata 版本控制信息表 默认 flyway_schema_history
    # 多个系统公用一个 数据库 schema 时配置spring.flyway.table 为不同的系统设置不同的
    # metadata 表名而不使用缺省值 flyway_schema_history 。
    table: flyway_schema_history
    # 如果没有 flyway_schema_history 这个 metadata 表， 在执行 flyway migrate 命令之前, 必须先执行 flyway baseline 命令
    # 设置为 true 后 flyway 将在需要 baseline 的时候, 自动执行一次 baseline。
    baseline-on-migrate: true
    # 指定 baseline 的版本号,默认值为 1, 低于该版本号的 SQL 文件, migrate 时会被忽略
    baseline-version: 1.0
    # 字符编码 默认 UTF-8
    encoding: UTF-8
    # 是否允许不按顺序迁移 开发建议 true  生产建议 false
    out-of-order: false
    # 需要 flyway 管控的 schema list,这里我们配置为flyway  缺省的话, 使用spring.datasource.url 配置的那个 schema,
    # 可以指定多个schema, 但仅会在第一个schema下建立 metadata 表, 也仅在第一个schema应用migration sql 脚本.
    # 但flyway Clean 命令会依次在这些schema下都执行一遍. 所以 确保生产 spring.flyway.clean-disabled 为 true
    # schemas: flyway
    # 执行迁移时是否自动调用验证   当你的 版本不符合逻辑 比如 你先执行了 DML 而没有 对应的DDL 会抛出异常
    validate-on-migrate: true
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    init-db: project
    url: jdbc:mysql://190.168.120.35:33336/project?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&&serverTimezone=Asia/Shanghai
    userName: root
    password: QWER@!@#$
    # 使用我们自己的druid数据源
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      # 初始化连接个数
      initialSize: 10
      # 最小连接个数
      minIdle: 5
      # 最大连接个数
      maxActive: 500
      # 最大等待时间
      maxWait: 60000
      # 配置检测可以关闭的空闲连接，间隔时间
      timeBetweenEvictionRunsMillis: 60000
      # 配置连接在池中的最小生存时间
      minEvictableIdleTimeMillis: 900000
      # 检测连接是否有，有效得select语句
      validationQuery: SELECT 1 FROM DUAL
      # 申请连接的时候检测，如果空闲时间大于time-between-eviction-runs-millis，执行validationQuery检测连接是否有效，建议配置为true，不影响性能，并且保证安全性。
      testWhileIdle: true
      # 申请连接时执行validationQuery检测连接是否有效，建议设置为false，不然会会降低性能
      testOnBorrow: false
      # 归还连接时执行validationQuery检测连接是否有效，建议设置为false，不然会会降低性能
      testOnReturn: false
      # 是否缓存preparedStatement，也就是PSCache  官方建议MySQL下建议关闭   个人建议如果想用SQL防火墙 建议打开
      # 打开PSCache，并且指定每个连接上PSCache的大小
      poolPreparedStatements: true

      maxPoolPreparedStatementPerConnectionSize: 20
      useGlobalDataSourceStat: true
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=1000
      ########### 启用内置过滤器（第一个 stat必须，否则监控不到SQL） 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙 ##########
      filters: stat,wall
      # 自己配置监控统计拦截的filter
      filter:
        # 开启druiddatasource的状态监控
        stat:
          enabled: true
          db-type: mysql
          # 开启慢sql监控，超过2s 就认为是慢sql，记录到日志中
          log-slow-sql: true
          slow-sql-millis: 2000
        # 日志监控，使用slf4j 进行日志输出
        slf4j:
          enabled: true
          statement-log-error-enabled: true
          statement-create-after-log-enabled: false
          statement-close-after-log-enabled: false
          result-set-open-after-log-enabled: false
          result-set-close-after-log-enabled: false
      ########## 配置WebStatFilter，用于采集web关联监控的数据 ##########
      web-stat-filter:
        enabled: true                   # 启动 StatFilter
        url-pattern: /*                 # 过滤所有url
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*" # 排除一些不必要的url
        session-stat-enable: true       # 开启session统计功能
        session-stat-max-count: 1000    # session的最大个数,默认100
      ########## 配置StatViewServlet（监控页面），用于展示Druid的统计信息 ##########
      stat-view-servlet:
        enabled: true                   # 启用StatViewServlet
        url-pattern: /druid/*           # 访问内置监控页面的路径，内置监控页面的首页是/druid/index.html
        reset-enable: false              # 不允许清空统计数据,重新计算
        login-userName: root            # 配置监控页面访问密码
        login-password: 123
        allow: work01.server.com           # 允许访问的地址，如果allow没有配置或者为空，则允许所有访问
        deny:
```

