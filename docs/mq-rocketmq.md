# 调用方启动类

```java
@EnableRocketmq
@MqListenerScan(listener = {Demo1MqListener.class}, basePackages = {"com.concise.mq.p2", "com.concise.mq.p1"})
@SpringBootApplication
public class MqApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }
}
```



> 默认@EnableRocketmq会使能rocketmq,
>
> 如果不指定 @EnableRocketmq 或者 @EnableRocketmq(false) 将关闭rocketmq, 项目启动时候不会进行初始化



# yaml文件

```yaml
rocketmq:
  name-server: work01.server.com:9876
  producer:
    # 如果不写组名, rocketmqTemplate不会初始化
    group: PRODUCE_APPNAME
    # 是否开启自动配置
    isOnOff: true
    # 消息最大长度 默认 1024 * 4 (4M)
    maxMessageSize: 4096
    # 发送消息失败重试次数，默认2
    retryTimesWhenSendFailed: 2
    # 发送消息超时时间，默认 3000
    sendMsgTimeOut: 3000
```

# 消费者

- 需要实现 `MqListener`接口
- 需要添加 `@Component` 注解

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



# 发布者

直接调用`RocketMqSendService` 接口即可

