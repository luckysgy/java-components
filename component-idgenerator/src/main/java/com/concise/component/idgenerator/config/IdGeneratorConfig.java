package com.concise.component.idgenerator.config;

import com.concise.component.idgenerator.IdGeneratorHandler;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Description: 漂移雪花算法 https://gitee.com/yitter/idgenerator/tree/master/Java
 * 文档地址 https://gitee.com/yitter/idgenerator/tree/master
 *
 * 能用多久
 * 1. 在默认配置下，ID可用 71000 年不重复。
 * 2. 在支持 1024 个工作节点时，ID可用 4480 年不重复。
 * 3， 在支持 4096 个工作节点时，ID可用 1120 年不重复。
 * @author shenguangyang
 * @date 2021/04/13
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(IdGeneratorProperties.class)
public class IdGeneratorConfig {
    @Autowired
    IdGeneratorProperties idGeneratorProperties;

    @Autowired(required = false)
    IdGeneratorHandler idGeneratorHandler;

    @PostConstruct
    public void init() {
        // 创建 IdGeneratorOptions 对象，请在构造函数中输入 WorkerId：
        IdGeneratorOptions options = new IdGeneratorOptions();
        options.BaseTime = idGeneratorProperties.getBaseTime();
        options.MaxSeqNumber = idGeneratorProperties.getMaxSeqNumber();
        options.MinSeqNumber = idGeneratorProperties.getMinSeqNumber();
        options.SeqBitLength = idGeneratorProperties.getSeqBitLength();
        options.WorkerId = idGeneratorProperties.getWorkerId();
        options.WorkerIdBitLength = idGeneratorProperties.getWorkerIdBitLength();
        if (idGeneratorHandler != null) {
            options.WorkerId = idGeneratorHandler.getWorkerId();
        }
        // 保存参数（必须的操作，否则以上设置都不能生效）：
        YitIdHelper.setIdGenerator(options);
        // 以上初始化过程只需全局一次，且必须在第2步之前设置。
    }
}
