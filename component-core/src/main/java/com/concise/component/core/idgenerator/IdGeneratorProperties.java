package com.concise.component.core.idgenerator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Description: 配置变更
 * 配置变更指是系统运行一段时间后，再变更运行参数（IdGeneratorOptions选项值），请注意：
 * 1.最重要的一条原则是：BaseTime 只能往前（比老值更小、距离现在更远）赋值，原因是往后赋值极大可能
 * 产生相同的时间戳。[不推荐在系统运行之后调整 BaseTime]
 *
 * 2.任何时候增加 WorkerIdBitLength 或 SeqBitLength，都是可以的，但是慎用 “减小”的操作，因为这
 * 可能导致在未来某天生成的 ID 与过去老配置时相同。[允许在系统运行之后增加任何一个 BitLength 值]
 *
 * 3.如果必须减小 WorkerIdBitLength 或 SeqBitLength 其中的一项，一定要满足一个条件：新的两个
 * BitLength 之和要大于 老的值之和。[不推荐在运行之后缩小任何一个 BitLength 值]
 *
 * 4.上述3条规则，并未在本算法内做逻辑控制，集成方应根据上述规则做好影响评估，确认无误后，再实施配置变更。
 * @author shenguangyang
 * @date 2021/04/13
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "id-generator")
public class IdGeneratorProperties {
    private long baseTime = 1582136402000L;
    /**
     * 机器码，最重要参数，无默认值，必须 全局唯一，必须 程序设定，缺省条件（WorkerIdBitLength取默认值）
     * 时最大值63，理论最大值 2^WorkerIdBitLength-1（不同实现语言可能会限定在 65535 或 32767，原
     * 理同 WorkerIdBitLength 规则）。不同机器或不同应用实例 不能相同，你可通过应用程序配置该值，也
     * 可通过调用外部服务获取值。针对自动注册WorkerId需求，本算法提供默认实现：通过 redis 自动注册
     * WorkerId 的动态库，详见“Tools\AutoRegisterWorkerId”。
     */
    private short workerId = 0;
    /**
     *  WorkerIdBitLength 默认值6，支持的 WorkerId 最大值为2^6-1(19)，若 WorkerId 超过64，
     *  可设置更大的 WorkerIdBitLength
     *
     *  一般来说，只要再设置 WorkerIdBitLength （决定 WorkerId 的最大值）。
     *  每增加 1位 WorkerIdBitLength 或 SeqBitLength，生成的ID数字值将会乘以2（基础长度可参考前一节“ID示例”），反之则除以2。
     */
    private byte workerIdBitLength = 6;
    /**
     * 序列数位长，默认值6，取值范围 [3, 21]（建议不小于4），决定每毫秒基础生成的ID个数。规则要求：
     * WorkerIdBitLength + SeqBitLength 不超过 22。
     */
    private byte seqBitLength = 6;
    /**
     * 最大序列数，设置范围 [MinSeqNumber, 2^SeqBitLength-1]，默认值0，真实最大序列数取最大值（2^SeqBitLength-1），
     * 不为0时，取其为真实最大序列数，一般无需设置，除非多机共享WorkerId分段生成ID（此时还要正确设置最小序列数）。
     */
    private short maxSeqNumber = 0;
    /**
     * 最小序列数，默认值5，取值范围 [5, MaxSeqNumber]，每毫秒的前5个序列数对应编号0-4是保留位，
     * 其中1-4是时间回拨相应预留位，0是手工新值预留位。
     */
    private short minSeqNumber = 5;
    private short topOverCostCount = 2000;
}
