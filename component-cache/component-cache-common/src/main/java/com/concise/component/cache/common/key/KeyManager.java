package com.concise.component.cache.common.key;

/**
 * 当key的长度不超过1024即1kb的长度的时候，基本上对性能不造成影响，但是一旦超过1024长度，
 * 随着key长度的增加，耗时也会随之增加。
 *
 * 所以，key长度对redis读写性能的影响是当key长度超过1024字节！因此我们在实际开发过程中
 * 可以根据自己的key长度预估对redis是否存在性能影响。
 *
 * 在实际业务开发中，基本上大家的key不会超过1024字节，因此可以在命名的时候，尽量取一些能见
 * 名知义的key，不必刻意为了缩短key长度而降低key的可读性。
 *
 * 当有这种key就必须特别长的时候，或者不确定是否超过1024字节，我们可以对key做一次hash后取
 * 哈希值作为redis的key，这样就可以大幅提高redis的性能了。这里推荐大家使用Murmurhash算法，算法详情见我的文章
 * https://www.cnblogs.com/CQqfjy/p/12254903.html#:~:text=%E5%BD%93key%E7%9A%84%E9%95%BF%E5%BA%A6%E4%B8%8D%E8%B6%85%E8%BF%871024%E5%8D%B31kb%E7%9A%84%E9%95%BF%E5%BA%A6%E7%9A%84%E6%97%B6%E5%80%99%EF%BC%8C%E5%9F%BA%E6%9C%AC%E4%B8%8A%E5%AF%B9%E6%80%A7%E8%83%BD%E4%B8%8D%E9%80%A0%E6%88%90%E5%BD%B1%E5%93%8D%EF%BC%8C%E4%BD%86%E6%98%AF%E4%B8%80%E6%97%A6%E8%B6%85%E8%BF%871024%E9%95%BF%E5%BA%A6%EF%BC%8C%E9%9A%8F%E7%9D%80key%E9%95%BF%E5%BA%A6%E7%9A%84%E5%A2%9E%E5%8A%A0%EF%BC%8C%E8%80%97%E6%97%B6%E4%B9%9F%E4%BC%9A%E9%9A%8F%E4%B9%8B%E5%A2%9E%E5%8A%A0%E3%80%82,%E6%89%80%E4%BB%A5%EF%BC%8Ckey%E9%95%BF%E5%BA%A6%E5%AF%B9redis%E8%AF%BB%E5%86%99%E6%80%A7%E8%83%BD%E7%9A%84%E5%BD%B1%E5%93%8D%E6%98%AF%E5%BD%93key%E9%95%BF%E5%BA%A6%E8%B6%85%E8%BF%871024%E5%AD%97%E8%8A%82%EF%BC%81%20%E5%9B%A0%E6%AD%A4%E6%88%91%E4%BB%AC%E5%9C%A8%E5%AE%9E%E9%99%85%E5%BC%80%E5%8F%91%E8%BF%87%E7%A8%8B%E4%B8%AD%E5%8F%AF%E4%BB%A5%E6%A0%B9%E6%8D%AE%E8%87%AA%E5%B7%B1%E7%9A%84key%E9%95%BF%E5%BA%A6%E9%A2%84%E4%BC%B0%E5%AF%B9redis%E6%98%AF%E5%90%A6%E5%AD%98%E5%9C%A8%E6%80%A7%E8%83%BD%E5%BD%B1%E5%93%8D%E3%80%82
 *
 * @author shenguangyang
 * @date 2021/7/31 21:09
 */
public interface KeyManager {
    /**
     * 过期时间
     * @return 单位是 s
     */
    int getExpireSeconds() ;

    /**
     * 获取 key
     * 推荐前缀命名规范: BasePrefix子类名:业务名:key变化值对应名称:key变化值:value名称
     * eg: key = SeckillKey:userId:orderId:123_23123123:userObject value: 用户对象
     * @param keyParams key参数
     * @return 缓存前缀
     */
    String getKey(String... keyParams);

    String getKey();

    String getHashKey(String... hashKeyParams);
}
