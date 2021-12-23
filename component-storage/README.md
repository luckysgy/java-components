# yaml

```yaml
# 文件存储配置
storageServer:
  enable: true
  # 如果没有type所对应的依赖(oss/minio), 是不会生效的
  type: ${STORAGE_TYPE}
  url:
    # url过期时间单位是s,用于具有时效性的链接
    expiryTime: 60
    # 内网url(局域网  Local Area Network) http://127.0.0.1
    lan: ${STORAGE_URL_INTRANET}
    # 外网(广域网 Wide Area Network) url http://47.78.12.56
    # 这里以 192.168.5.248 当做外网测试
    wan: http://192.168.5.248:9090/files
  minio:
    accessKey: ${STORAGE_MINIO_ACCESSKEY}
    secretKey: ${STORAGE_MINIO_SECRETKEY}
    endpoint: ${STORAGE_MINIO_ENDPOINT}
  oss:
    secretAccessKey: ${STORAGE_OSS_SECRETACCESSKEY}
    accessKeyId: ${STORAGE_OSS_ACCESSKEYID}
    endpoint: ${STORAGE_OSS_ENDPOINT}
    # 是否使能代理，如果使能nginx代理，则获取持久链接的前缀
    # 就是url中的lan或者wan作为前缀
    proxy:
      enable: false
```

# 桶名

鉴于桶名不会经常改变且为了方便定义不同桶名，采用扩展的方式初始化所有桶名