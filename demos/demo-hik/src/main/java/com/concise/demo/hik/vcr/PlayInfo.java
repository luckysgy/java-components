package com.concise.demo.hik.vcr;

import com.concise.component.core.exception.Assert;
import lombok.Data;

import java.io.Serializable;

/**
 * 播放信息
 * @author shenguangyang
 * @date 2021-12-05 15:52
 */
@Data
public class PlayInfo implements Serializable {
    private String streamId;
    private Integer code;
    private String message;

    public PlayInfo(String streamId, PlayCode playCode) {
        Assert.notNull(streamId, "streamId != null");
        this.streamId = streamId;
        this.code = playCode.getCode();
        this.message = playCode.getMessage();
    }
    public PlayInfo(String streamId, PlayCode playCode, String message) {
        Assert.notNull(streamId, "streamId != null");
        this.streamId = streamId;
        this.code = playCode.getCode();
        this.message = message;
    }

    public PlayInfo() {
    }
}
