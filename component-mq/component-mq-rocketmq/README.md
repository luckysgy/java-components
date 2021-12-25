# yaml文件

```yaml
rocketmq:
  name-server: work01.server.com:9876
  producer:
    # 是否开启自动配置
    isOnOff: true
    # 消息最大长度 默认 1024 * 4 (4M)
    maxMessageSize: 4096
    # 发送消息失败重试次数，默认2
    retryTimesWhenSendFailed: 2
    # 发送消息超时时间，默认 3000
    sendMsgTimeOut: 3000
```
