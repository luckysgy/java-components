# 变更日志

2022年2月

- 去掉多个桶管理, 一个项目只管理一个桶, 桶中的一级目录作为区分不同业务来替代原先多个桶, see `StorageBucketManage`
- 将StorageService由抽象类改成接口