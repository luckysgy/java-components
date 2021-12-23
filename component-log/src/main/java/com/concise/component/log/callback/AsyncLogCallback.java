package com.concise.component.log.callback;

import com.concise.component.log.entity.OperLogDTO;

/**
 * 保存日志回调
 *
 * 内部会自动回调 {@link #saveLog(OperLogDTO)} 方法, 因此需要自己实现
 * AsyncLogCallback
 *
 * 注意: 实现类要加入到spring容器中
 * @author shenguangyang
 * @date 2021-11-06 19:40
 */
public interface AsyncLogCallback {
    void saveLog(OperLogDTO operLogDTO);
}
