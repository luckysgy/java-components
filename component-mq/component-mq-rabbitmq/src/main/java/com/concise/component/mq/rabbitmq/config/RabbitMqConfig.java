package com.concise.component.mq.rabbitmq.config;

import com.concise.component.core.utils.CollectionUtils;
import com.concise.component.core.utils.SpringUtils;
import com.concise.component.core.utils.StringPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shenguangyang
 * @date 2021-10-05 12:23
 */
@Component
@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Resource
    private RabbitMqProperties rabbitMqProperties;

    @Resource
    private CachingConnectionFactory connectionFactory;

    @Resource
    private MessageConfirmCallback messageConfirmCallback;

    @Resource
    private MessageReturnsCallback messageReturnsCallback;

    private List<ExchangeConfig> getExchangeConfigList() {
        List<ExchangeConfig> result = new ArrayList<>();
        Map<String, ExchangeConfig> exchanges = rabbitMqProperties.getExchanges();
        for (Map.Entry<String, ExchangeConfig> entry : exchanges.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    private List<QueueConfig> getQueueConfigList() {
        List<QueueConfig> result = new ArrayList<>();
        Map<String, QueueConfig> exchanges = rabbitMqProperties.getQueues();
        for (Map.Entry<String, QueueConfig> entry : exchanges.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }


    public ExchangeConfig getExchangeConfig(String name) {
        Map<String, ExchangeConfig> collect = getExchangeConfigList().stream().collect(Collectors.toMap(ExchangeConfig::getName, e -> e));
        return collect.get(name);
    }

    /**
     * 模板的初始化配置
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        // 开启事务配置
        // 开启事务会大幅降低消息发送及接收效率，因为当已经有一个事务存在时，后面的消息是不能被发送或者接收（对同一个consumer而言）的。
        //rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.setConfirmCallback(messageConfirmCallback);
        rabbitTemplate.setReturnsCallback(messageReturnsCallback);
        return rabbitTemplate;
    }

    /**
     * 动态创建交换机
     */
    @Bean
    public Object createExchange() {
        log.info("start create exchange");
        List<ExchangeConfig> exchanges = getExchangeConfigList();
        if (!CollectionUtils.isEmpty(exchanges)) {
            exchanges.forEach(e -> {
                log.info("create exchange: {}", e.getName());
                // 声明交换机
                Exchange exchange = null;
                switch (e.getType()) {
                    case DIRECT:
                        exchange = ExchangeBuilder.directExchange(e.getName()).durable(e.getDurable()).build();
                        break;
                    case TOPIC:
                        exchange = ExchangeBuilder.topicExchange(e.getName()).durable(e.getDurable()).build();
                        break;
                    case HEADERS:
                        exchange = ExchangeBuilder.headersExchange(e.getName()).durable(e.getDurable()).build();
                        break;
                    case FANOUT:
                        exchange = ExchangeBuilder.fanoutExchange(e.getName()).durable(e.getDurable()).build();
                        break;
                    default:
                        log.warn("exchange type not exist: " + e.getType() + ", see ExchangeType class");
                        break;
                }
                // 将交换机注册到spring bean工厂 让spring实现交换机的管理
                if (exchange != null) {
                    SpringUtils.registerBean(e.getName(), exchange);
                }
            });
        }
        log.info("end create exchange");
        return null;
    }

    /**
     * 动态绑定队列和交换机
     */
    @Bean
    public Object bindingQueueToExchange() {
        log.info("start binding queue to exchange");
        List<QueueConfig> queues = getQueueConfigList();
        if (!CollectionUtils.isEmpty(queues)) {
            queues.forEach(q -> {
                // 创建队列
                Queue queue = new Queue(q.getName(), q.getDurable(),
                        q.getExclusive(), q.getAutoDelete(), q.getArgs());

                // 注入队列bean
                SpringUtils.registerBean(q.getName(), queue);

                // 获取队列绑定交换机名
                List<String> exchangeNameList;
                if (q.getExchangeName().contains(StringPool.COMMA)) {
                    String[] split = q.getExchangeName().split(StringPool.COMMA);
                    exchangeNameList = Arrays.asList(split);
                } else {
                    exchangeNameList = Collections.singletonList(q.getExchangeName());
                }

                exchangeNameList.forEach(name -> {
                    // 获取交换机配置参数
                    ExchangeConfig exchangeConfig = getExchangeConfig(name);
                    Binding binding = bindingBuilder(queue, q, exchangeConfig);

                    // 将绑定关系注册到spring bean工厂 让spring实现绑定关系的管理
                    if (binding != null) {
                        log.info("queue [{}] binding exchange [{}] success!", q.getName(), exchangeConfig.getName());
                        SpringUtils.registerBean(q.getName() + StringPool.DASH + name, binding);
                    }
                });
            });
        }
        log.info("end binding queue to exchange");
        return null;
    }

    public Binding bindingBuilder(Queue queue, QueueConfig q, ExchangeConfig exchangeConfig) {
        // 声明绑定关系
        Binding binding = null;

        // 根据不同的交换机模式 获取不同的交换机对象（注意：刚才注册时使用的是父类Exchange，这里获取的时候将类型获取成相应的子类）生成不同的绑定规则
        switch (exchangeConfig.getType()) {
            case TOPIC:
                binding = BindingBuilder.bind(queue)
                        .to(SpringUtils.getBean(exchangeConfig.getName(), TopicExchange.class))
                        .with(q.getRoutingKey());
                break;
            case DIRECT:
                binding = BindingBuilder.bind(queue)
                        .to(SpringUtils.getBean(exchangeConfig.getName(), DirectExchange.class))
                        .with(q.getRoutingKey());
                break;
            case HEADERS:
                if (q.getWhereAll()) {
                    binding = BindingBuilder.bind(queue)
                            .to(SpringUtils.getBean(exchangeConfig.getName(), HeadersExchange.class))
                            .whereAll(q.getHeaders()).match();
                } else {
                    binding = BindingBuilder.bind(queue)
                            .to(SpringUtils.getBean(exchangeConfig.getName(), HeadersExchange.class))
                            .whereAny(q.getHeaders()).match();
                }
                break;
            case FANOUT:
                binding = BindingBuilder.bind(queue)
                        .to(SpringUtils.getBean(exchangeConfig.getName(), FanoutExchange.class));
                break;
//            case CUSTOM:
//                binding = BindingBuilder.bind(queue)
//                        .to(SpringUtils.getBean(exchangeConfig.getName(), CustomExchange.class))
//                        .with(q.getRoutingKey()).noargs();
//                break;
            default:
                log.warn("queue [{}] config unspecified exchange!", q.getName());
                break;
        }

        return binding;
    }

}
