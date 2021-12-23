package com.concise.component.cache.redis;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;

/**
 * redis实现布隆过滤器
 * @author shenguangyang
 * @date 2021/8/1 10:07
 */
@ConfigurationProperties("bloom.filter")
@Component
public class RedisBloomFilter {
    /**
     * 预计数据总量
     */
    private long expectedInsertions;
    /**
     * 容错率
     */
    private double fpp;
    /**
     * 二进制向量大小
     */
    private long numBits;
    /**
     * 哈希算法数量
     */
    private int numHashFunctions;
    /**
     * redis中的key
     */
    private final String key = "gy_bloom_filter";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 初始化
     */
    @PostConstruct
    private void init(){
        numBits = optimalNumOfBits();
        numHashFunctions = optimalNumOfHashFunctions();
    }

    // 向布隆过滤器中put
    public void put(String value){
        long[] indexs = getIndexs(value);
        // 将对应下标改为1
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.openPipeline();
                for (long index : indexs) {
                    redisConnection.setBit(key.getBytes(), index,true);
                }
                redisConnection.close();
                return null;
            }
        });
    }

    /**
     * 判断value是否可能存在
     * @param value
     * @return 是返回true , 否返回false
     */
    public boolean isExist(String value){
        long[] indexs = getIndexs(value);
        // 只要有一个bit位为0就表示不可能存在
        List list = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.openPipeline();
                for (long index : indexs) {
                    redisConnection.getBit(key.getBytes(), index);
                }
                redisConnection.close();
                return null;
            }
        });
        return !list.contains(false);
    }

    /**
     * 根据key获取bitmap下标(算法借鉴)
     * @param vaule
     * @return
     */
    private long[] getIndexs(String vaule) {
        long hash1 = hash(vaule);
        long hash2 = hash1 >>> 16;
        long[] result = new long[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            long combinedHash = hash1 + i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    /**
     * 获取一个hash值 方法来自guava
     * @param vaule
     * @return
     */
    private long hash(String vaule) {
        Charset charset = Charset.defaultCharset();
        return Hashing.murmur3_128().hashObject(vaule, Funnels.stringFunnel(charset)).asLong();
    }

    /**
     * 计算二进制向量大小(算法借鉴)
     * @return
     */
    private long optimalNumOfBits(){
        if (fpp == 0) {
            fpp = Double.MIN_VALUE;
        }
        return (long) (-expectedInsertions * Math.log(fpp) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算哈希算法数量(算法借鉴)
     * @return
     */
    private int optimalNumOfHashFunctions() {
        return Math.max(1, (int) Math.round((double) numBits / expectedInsertions * Math.log(2)));
    }

    public void setExpectedInsertions(long expectedInsertions) {
        this.expectedInsertions = expectedInsertions;
    }

    public void setFpp(double fpp) {
        this.fpp = fpp;
    }

}
