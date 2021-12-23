package com.concise.component.idgenerator;

/**
 * Description: 回调处理
 *
 * @author shenguangyang
 * @date 2021/04/13
 */
public interface IdGeneratorHandler {
    /**
     * 注册全局唯一WorkerId，才能根据它生产唯一ID。
     * @return WorkerId 返回值会覆盖掉配置文件中配置的WorkerId
     */
    short getWorkerId();
}
