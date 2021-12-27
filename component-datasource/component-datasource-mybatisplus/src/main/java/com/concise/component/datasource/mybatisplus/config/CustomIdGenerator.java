package com.concise.component.datasource.mybatisplus.config;


import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.stereotype.Component;

/**
 * 自定全局唯一id生成器
 * @author shenguangyang
 * @date 2021/6/19 20:49
 */
@Component
public class CustomIdGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        return YitIdHelper.nextId();
    }
}
