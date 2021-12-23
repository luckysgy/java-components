package com.concise.component.cache.common.service;

/**
 * 缓存服务
 * @author shenguangyang
 * @date 2021-10-02 上午6:19
 */
public interface CacheService {
    KeyOps opsKey();
    ListOps opsList();
    ValueOps opsValue();
    HashOps opsHash();
}
