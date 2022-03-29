
```java
@Data
@TableName("demo")
public class DemoPO {
    @TableId(value = "demo_id", type = IdType.AUTO)
    private Long demoId;

    private String name;
}

// 推荐: 代码中主键使用string, 数据库中使用bigint
@Data
@TableName("demo")
public class DemoPO {
//    @TableId(value = "demo_id", type = IdType.AUTO)
//    private String demoId;

    // 如果使用了自定义主键类型, 不需要指定type
    @TableId(value = "demo_id")
    private String demoId;

    private String name;
}
```