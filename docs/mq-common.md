# 扫描消息队列监听器

在调用方的启动类上, 添加如下注解实现扫描所需要的监听消息类

- `basePackages`用于指定监听者所在包名
- `listener`指定使能的监听者

```java
@MqListenerScan(listener = {Demo1MqListener.class}, basePackages = {"com.concise.mq.p2", "com.concise.mq.p1"})
@SpringBootApplication
public class MqApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }
}
```



> 注意: 监听类需要满足如下条件
>
> 1. 需要有 `@Component` 注解
> 2. 需要实现 `MqListener`接口



监听者例子: 

```java
@Component
@RocketMQMessageListener(
        nameServer = "${rocketmq.name-server}",
        topic = "demo2",
        consumerGroup = "demo2",
        selectorExpression = "demo2")
public class Demo2MqListener implements MqListener, RocketMQListener<RocketMqMessage<String>> {
    private static final Logger log = LoggerFactory.getLogger(Demo2MqListener.class);

    @PostConstruct
    public void init() {
        log.info("init Demo2MqListener");
    }
    public Demo2MqListener() {
        System.out.println("----------------------------------+++++++++++++++++++Demo2MqListener");
    }

    @Override
    public void onMessage(RocketMqMessage<String> stringRocketMqMessage) {
        System.out.println(this.getClass().getName());
        System.out.println(stringRocketMqMessage.getContent());
    }
}
```



# 提供本地消息队列

定义一个类继承 `LocalMessageQueue` 即可

