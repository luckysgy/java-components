# 实现的功能

- 保证消息的99.99999%不会丢失
- 解决重复消费问题

# 如何使用

## 重要的五个类

`MqMessage`: 所有的消息实体类都需要继承该类

`MqConsumer`: 所有的消费者都需要定义一个接口继承该类, 然后实现自定义的接口(多定义一个接口是可选的)

`MqProducer`: 所有的生产者都需要定义一个接口继承该类, 然后实现自定义的接口(多定义一个接口是可选的)

`MqListener`: 需要在生产者中调用该类中的exec方法(可选)

`MqSendFailHandle`: 当消息重复投递次数达到10次 ()`RabbitMqMessage` 类中配置), 之后会主动回调`MqSendFailHandle#reachMaxRetryCount` 方法, 需要开发者实现该类做一些处理, 比如进行人工干预等等

## 邮箱例子

> 生产者

```java
/**
 * @author shenguangyang
 * @date 2021-10-05 16:06
 */
@Component
public class EmailMqProducer implements IEmailMqProducer {
    private static final Logger log = LoggerFactory.getLogger(EmailMqProducer.class);

    @Autowired
    private MqSendService mqSendService;

    /**
     * consumer {@link IEmailMqConsumer}
     * @param mqMessage 消息内容
     */
    @Override
    public void produce(EmailMessage mqMessage) {

        // 给消息设置过期时间
//        MessagePostProcessor messagePostProcessor = message -> {
//            // 5s之后会过期
//            message.getMessageProperties().setExpiration("5000");
//            message.getMessageProperties().setContentEncoding("utf-8");
//            return message;
//        };

        log.info("ExchangeNames.EMAIL: {} , mqMessage: {}", MqConstant.EMAIL_EXCHANGE, mqMessage);
        mqSendService.send(MqConstant.EMAIL_EXCHANGE, "", mqMessage);
    }
}
```



> 消费者

```java
/**
 * @author shenguangyang
 * @date 2021-10-05 16:02
 */
@Service
public class EmailMqConsumer implements IEmailMqConsumer {
    private static final Logger log = LoggerFactory.getLogger(EmailMqConsumer.class);

    /**
     * producer {@link IEmailMqProducer}
     * @param mqMessage
     */
    @Override
    public void consume(EmailMessage mqMessage) {
        log.info(mqMessage.toString());
    }

    @Component
    @RabbitListener(queues = MqConstant.EMAIL_QUEUE)
    public static class EmailMqListener  {
        private static final Logger log = LoggerFactory.getLogger(EmailMqListener.class);

        @Autowired
        private EmailMqConsumer emailMqConsumer;

        @Autowired
        private MqListener mqListener;

        private static int count = 10;

        @RabbitHandler
        public void onMessage(String msg, Channel channel, Message message) {
            try {
                //消息可以通过msg获取也可以通过message对象的body值获取
                mqListener.exec(msg, emailMqConsumer, EmailMessage.class);

                if (count < 2) {
                    count++;
                    int i = 10 / 0;
                }

                /*
                  因为我在application.yml那里配置了消息手工确认也就是传说中的ack,所以消息消费后必须发送确认给mq
                  很多人不理解ack(消息消费确认),以为这个确认是告诉消息发送者的,这个是错的,这个ack是告诉mq服务器,
                  消息已经被我消费了,你可以删除它了
                  如果没有发送basicAck的后果是:每次重启服务,你都会接收到该消息
                  如果你不想用确认机制,就去掉application.yml的acknowledge-mode: manual配置,该配置默认
                  是自动确认auto,去掉后,下面的channel.basicAck就不用写了
                 */
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (Exception e) {
                try {
                    /*
                     * 用于否定确认，但与channel.basicNack相比有一个限制:一次只能拒绝单条消息
                     * 第二个参数，true会重新放回队列，所以需要自己根据业务逻辑判断什么时候使用拒绝
                     *
                     * 拒绝消费当前消息，如果第二参数传入true，就是将数据重新丢回队列里，那么下次还会消费这消息。设置false，
                     * 就是告诉服务器，我已经知道这条消息数据了，因为一些原因拒绝它，而且服务器也把这个消息丢掉就行。 下次不
                     * 想再消费这条消息了。使用拒绝后重新入列这个确认模式要谨慎，因为一般都是出现异常的时候，catch异常再拒绝入列，选择是否重入列。
                     * 但是如果使用不当会导致一些每次都被你重入列的消息一直消费-入列-消费-入列这样循环，会导致消息积压。
                     */
                    // channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

                    /*
                     * 用于否定确认
                     * 第一个参数依然是当前消息到的数据的唯一id;
                     * 第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。
                     * 第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去。
                     * 同样使用不确认后重新入列这个确认模式要谨慎，因为这里也可能因为考虑不周出现消息一直被重新丢回去的情况，导致积压。
                     */
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);

                    /*
                     * basic.recover是否恢复消息到队列，参数是是否requeue，true则重新入队列，并且尽可能的将之前recover的
                     * 消息投递给其他消费者消费，而不是自己再次消费。false则消息会重新被投递给自己。
                     */
                    // channel.basicRecover(true);
                } catch (Exception ex) {
                    log.error("EmailMqListener basicNack fail: {}", ex.getMessage());
                    return;
                }
                log.error("EmailMqListener exec fail: {}", e.getMessage());
            }
        }
    }
}
```



## 订单例子

模拟在指定时间内没有支付订单, 则自动取消订单



> 生产者

```java
/**
 * @author shenguangyang
 * @date 2021-10-06 14:32
 */
@Component
public class OrderMqProducer implements IOrderMqProducer {
    @Autowired
    private MqSendService mqSendService;
    private static final Logger log = LoggerFactory.getLogger(OrderMqProducer.class);

    /**
     * 如果订单长时间没有被消费会转移到死信队列中, 由 {@link IOrderExpiredMqConsumer} 进行消费
     * @param mqMessage 消息内容
     */
    @Override
    public void produce(OrderMessage mqMessage) {
        log.info(" MqConstant.ORDER_EXCHANGE: {} , mqMessage: {}", MqConstant.ORDER_EXCHANGE, mqMessage);
        mqSendService.send(MqConstant.ORDER_EXCHANGE, "", mqMessage);
    }
}
```



> 超时为支付时, 消息队列回调(消费者)，此时消费的是死信队列中的消息

```java
/**
 * 订单超时未支付
 * @author shenguangyang
 * @date 2021-10-06 14:37
 */
@Component
public class OrderExpiredMqConsumer implements IOrderExpiredMqConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderExpiredMqConsumer.class);

    /**
     * 订单超时未支付会被转移到死信队列中了 {@link MqConstant#ORDER_EXPIRED_QUEUE}
     * 在这个方法中做如下事情
     * 1. 判断用户是否已经支付
     * 2. 如果已经支付则返回
     * 3. 如果未支付则取消订单
     * @param mqMessage
     */
    @Override
    public void consume(OrderMessage mqMessage) {
        log.info("订单超时未支付: {}", mqMessage.toString());
    }

    @Component
    @RabbitListener(queues = MqConstant.ORDER_EXPIRED_QUEUE)
    public static class OrderExpiredMqListener  {
        private static final Logger log = LoggerFactory.getLogger(OrderExpiredMqConsumer.OrderExpiredMqListener.class);

        @Autowired
        private OrderExpiredMqConsumer orderExpiredMqConsumer;
        
        @Autowired
        private MqListener mqListener;

        @RabbitHandler
        public void onMessage(String msg, Channel channel, Message message) {
            try {
                //消息可以通过msg获取也可以通过message对象的body值获取
                mqListener.exec(msg, orderExpiredMqConsumer, OrderMessage.class);

                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

            } catch (IOException e) {
                //出现异常,告诉mq抛弃该消息
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
}
```

# 启动类配置

```java
// 在启动类上添加如下注解
@EnableRabbit
```





# 应用层中的application.yaml

```yaml
server:
  port: 8081

# 日志配置
logging:
  level:
    # 注意注意注意 一定要修改成自己的包名
    # 如果你只需要输出info，可以不用修改成自己的包名，但是如果为其他级别，
    # 比如debug必修改成自己的包名才会生效
    com.simplifydev: debug
  file:
    path: /home/app/logs/

spring:
  profiles:
    active: dev,mq
  application:
    name: rabbitmq
  #reids配置
  # Redis数据库索引（默认为0）
  redis:
    database: 0
    # Redis服务器地址
    host: work01.server.com
    # Redis服务器连接端口
    port: 36379
    # Redis服务器连接密码（默认为空）
    password: QWER@123456
    lettuce:
      pool:
        #连接池最大连接数（使用负值表示没有限制）
        max-active: 100
        # 连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait: -1ms
        # 连接池中的最大空闲连接
        max-idle: 8
        # 连接池中的最小空闲连接
        min-idle: 0
  rabbitmq:
    host: work01.server.com
    port: 5672
    username: admin
    password: shenguangyang@97
    virtualHost: /
    publisher-returns: true  #消息发送后,如果发送失败,则会返回失败信息信息
    #connection-timeout: 15000
    connection-timeout: 1500
    template:
      # 触发returnedMessage回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
      mandatory: true
    listener: #加了2下面2个属性,消费消息的时候,就必须发送ack确认,不然消息永远还在队列中
      direct:
        # 手动确认
        acknowledge-mode: manual
      simple:
        # 手动确认
        acknowledge-mode: manual
        # 每次从RabbitMQ获取的消息数量
        prefetch: 1
        default-requeue-rejected: false
        # 每个队列启动的消费者数量
        concurrency: 1
        # 每个队列最大的消费者数量
        max-concurrency: 1
        retry:
          #最大重试次数
          max-attempts: 5
          # 是否开启消费者重试（为false时关闭消费者重试，这时消费端代码异常会一直重复收到消息）
          enabled: true
          #最大间隔时间
          max-interval: 20000ms
          # 重试间隔时间
          initial-interval: 3000ms
          #乘子  重试间隔*乘子得出下次重试间隔  3s  6s  12s  24s  此处24s>20s  走20s
          multiplier: 2

    # 消息发送后,如果发送成功到队列,则会回调成功信息
    publisher-confirm-type: correlated
```



> 为了使公共层中的application-mq.yaml生效, 需要激活mq文件, 即spring.profiles.active=dev,mq

# 公共层中的application-mq.yaml

```yaml
rabbitmq:
  # 交换机
  exchanges:
    # 通配符订阅
    topicLog:
      name: log.topic.exchange
      type: TOPIC
    # 广播
    email:
      name: email.fanout.exchange
      type: FANOUT
    # 消息头
    headers:
      name: headers.exchange
      type: HEADERS
    # 死信交换机
    orderExpired:
      name: order.dead.direct.exchange
      type: DIRECT
    order:
      name: order.direct.exchange
      type: DIRECT
  # 队列
  queues:
    # 定义死信队列
    orderExpired:
      exchange-name: order.dead.direct.exchange
      name: order.dead.direct.queue
    order:
      name: order.direct.queue
      args:
        # 设置队列中消息的过期时间, 60s之后会被转移到死信队列中
        x-message-ttl: 60000
        # 当消息过期时候, 需要通过指定的交换机打入到死信队列中
        x-dead-letter-exchange: order.dead.direct.exchange
      exchange-name: order.direct.exchange

    # 直连队列
    headers:
      name: headers.queue
      headers:
        test: 123
      exchange-name: headers.exchange
    email:
      name: email.fanout.queue
      exchange-name: email.fanout.exchange
    # 队列2
    logError:
      name: log.error.queue
      routing-key: queue.info.error.*
      exchange-name: log.topic.exchange
    # 队列2
    logInfo:
      name: log.info.queue
      routing-key: queue.log.info.*
      exchange-name: log.topic.exchange

```

# 扩展

## 消息丢失问题

### **说明**

对于消息队列（MQ）来说，消息丢失/消息重复/消费顺序/消息堆积是比较常见的问题，这几个问题比较重要面试中也会经常问到。

### **消息丢失的场景**

首先明确一条消息的传送流程：生产者->MQ->消费者

所以这三个节点都可能丢失数据：

- Producer端
  发送消息过程中出现网络问题：producer以为发送成功，但RabbitMQ server没有收到；

- RabbitMQ server 端
  接收到消息后由于服务器宕机或重启等原因（消息默认存在内存中）导致消息丢失；

  由于服务器宕机或者重启，即将发送给消息队列的消息会发生丢失现象

- Consumer端
  Consumer端接收到消息后自动返回ack，但后边处理消息出错，没有完成消息的处理；

### **生产者丢失消息**

生产者将数据发送到RabbitMQ的时候，可能因为网络问题导致数据没到达RabbitMQ Server。

 生产者将信道设置成confirm（确认）模式，一旦信道进入confirm模式，所有在该信道上面发布的消息都会被指派一个唯一的ID(从1开始)，一旦消息被投递到所有匹配的队列之后，RabbitMQ就会发送一个确认（Basic.Ack)给生产者（包含消息的唯一ID)，这就使得生产者知晓消息己经正确到达了目的地了。如果消息和队列是可持久化的，那么确认消息会在消息写入磁盘之后发出。

RabbitMQ回传给生产者的确认消息中的deliveryTag包含了确认消息的序号，此外RabbitMQ也可以设置channel.basicAck方法中的multiple参数，表示不到这个序号之前的所有消息都已经得到了处理。

如果RabbitMQ因为自身内部错误导致消息丢失，就会发送一条nack(Basic.Nack)命令。

​		事务机制在一条消息发送之后会使发送端阻塞，以等待RabbitMQ的回应，之后才能继续发送下一条消息。相比之下，发送方确认机制最大的好处在于它是异步的，一旦发布一条消息，生产者应用程序就可以在等信道返回确认的同时继续发送下一条消息，当消息最终得到确认之后，生产者应用程序便可以通过回调方法来处理该确认消息。（发送完一个消息之后就可以发送下一个消息）。



引入新问题：

问题：如果RabbitMQ服务端正常接收到了，把ack信息发送给生产者，结果这时网断了，怎么办？

解决方案：在内存里维护每个消息id的状态以及其发送的时间，然后启动一个定时线程去检查它，若超过一定时间还没接收到这个消息的回调，那么就重发。此时，消费者就要处理幂等问题（多次接收到同一条消息）。



引入另一个问题:

问题: 一开始由于网络原因或者服务器宕机或者重启，这时候发送消息会出现丢失现场, 怎么办?

解决方案: 将发送异常的消息写入到redis中 (因为考虑到网络原因, 最好在本地也写一份，本例子中没有实现这一点)，然后定时拉取发送失败的消息进行重发

### **Broker丢失消息**

**消息丢失场景**
        RabbitMQ服务端接收到消息后由于服务器宕机或重启等原因（消息默认存在内存中）导致消息丢失；

**解决方案：开启Broker持久化**
**结论**

​    为防止RabbitMQ服务端弄丢数据，要开启RabbitMQ的持久化，就是消息写入之后会持久化到磁盘，哪怕是RabbitMQ自己挂了，恢复之后会自动读取之前存储的数据，一般数据不会丢。

**详解**

　　设置持久化有两个步骤，第一个是创建queue的时候将其设置为持久化的，这样就可以保证RabbitMQ持久化queue的元数据，但是不会持久化queue里的数据；第二个是发送消息的时候将消息的deliveryMode设置为2，就是将消息设置为持久化的，此时RabbitMQ就会将消息持久化到磁盘上去。必须要同时设置这两个持久化才行，RabbitMQ哪怕是挂了，再次重启，也会从磁盘上重启恢复queue，恢复这个queue里的数据。

　　极其罕见的是，RabbitMQ还没持久化，自己就挂了，导致数据丢失，但是这个概率较小。当然，也可以解决，解决方法如下：

　　RabbitMQ开启持久化，生产者开启confirm机制。只有消息被持久化到磁盘之后，才会通知生产者ack了，所以哪怕是在持久化到磁盘之前，RabbitMQ挂了，数据丢了，生产者收不到ack，你也是可以自己重发的。

### **消费者丢失消息**

**消息丢失场景**
情景1：RabbitMQ服务端向消费者发送完消息之后，网络断了，消息并没有到达消费者（RabbitMQ服务端的消息此时已删除）。

情景2：Consumer端接收到消息后自动返回ack，但后边处理消息出错，没有完成消息的处理。

**解决方案**

- 结论

设置返回确认的模式为手动，并在处理完消息后手动去提交确认。(做法：消费者在订阅队列时，指定autoAck为false)

- 详解

​      当设置返回确认的模式为手动（autoAck参数置为false）,对于RabbitMQ服务端而言，队列中的消息分成了两个部分：一部分是等待投递给消费者的消息；一部分是已经投递给消费者，但是还没有收到消费者确认信号的消息。如果RabbitMQ—直没有收到消费者的确认信号，并且消费此消息的消费者己经断开连接，则RabbitMQ会安排该消息重新进入队列，等待投递给下一个消费者，当然也有可能还是原来的那个消费者。

​      RabbitMQ不会为未确认的消息设置过期时间，它判断此消息是否需要重新投递给消费者的唯一依据是消费该消息的消费者连接是否已经断开，这么设计的原因是RabbitMQ允许消费者消费一条消息的时间可以很久很久。

