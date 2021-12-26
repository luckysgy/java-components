package com.concise.demo.hik.vcr;

import com.concise.component.core.exception.Assert;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 录像机正在回放的信息
 * @author shenguangyang
 * @date 2021-12-04 16:33
 */
@Getter
@Setter
@ToString
public class VcrPlayingInfo implements Serializable {
    private VcrPlayInfo playInfo;
    /**
     * 回放句柄
     */
    private Integer playHandle;

    private String streamId;

    public VcrPlayingInfo(String streamId, VcrPlayInfo playInfo, Integer playHandle) {
        Assert.notNull(streamId, "streamId != null");
        this.playInfo = playInfo;
        this.playHandle = playHandle;
        this.streamId = streamId;
    }

    public VcrPlayingInfo() {
    }
}
