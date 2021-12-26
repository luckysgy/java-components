package com.concise.demo.hik.vcr;

import com.alibaba.fastjson.annotation.JSONField;
import com.concise.component.core.exception.Assert;
import com.concise.demo.hik.sdk.HCNetSDK;
import lombok.Data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 回放信息
 * @author shenguangyang
 * @date 2021-12-04 16:07
 */
@Data
public class VcrPlayInfo implements Serializable {
    /**
     * streamId 播放流id, 需要用户自己随机定义一个字符串作为播放流的标识, eg: uuid
     */
    private String streamId;
    /**
     * 通道号
     */
    private Integer channel;

    /**
     * 下载视频的起始时间戳, 实际下载的视频是精确到s的
     */
    private Long startTimestamp;

    /**
     * 下载视频的结束时间戳, 实际下载的视频是精确到s的
     */
    private Long endTimestamp;

    @JSONField(serialize = false)
    private HCNetSDK.NET_DVR_TIME dvrStartTime;

    @JSONField(serialize = false)
    private HCNetSDK.NET_DVR_TIME dvrEndTime;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public VcrPlayInfo(String streamId, Integer channel, Long startTimestamp, Long endTimestamp) {
        Assert.isTrue(startTimestamp != null && startTimestamp > 0 && endTimestamp != null && endTimestamp > 0,
                "startTimestamp != null && startTimestamp > 0 && endTimestamp != null && endTimestamp > 0");
        Assert.isTrue(channel > 0, "channel > 0");
        Assert.isTrue(endTimestamp > startTimestamp, "endTimestamp > startTimestamp");
        Assert.notNull(streamId, "streamId not null");
        this.streamId = streamId;
        this.channel = channel;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        dvrStartTime = VcrUtils.getDvrTime(startTimestamp);
        dvrEndTime = VcrUtils.getDvrTime(endTimestamp);
    }

    /**
     * @param startTimeStr 起始时间, 格式yyyy-MM-dd HH:mm:ss
     * @param endTimeStr 结束时间, 格式yyyy-MM-dd HH:mm:ss
     */
    public VcrPlayInfo(String streamId, Integer channel, String startTimeStr, String endTimeStr) throws ParseException {
        this(streamId, channel, sdf.parse(startTimeStr).getTime(), sdf.parse(endTimeStr).getTime());
    }

    public VcrPlayInfo() {
    }
}
