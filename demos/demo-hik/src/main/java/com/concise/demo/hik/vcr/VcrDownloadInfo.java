package com.concise.demo.hik.vcr;

import com.concise.component.core.utils.StringUtils;
import com.concise.demo.hik.sdk.HCNetSDK;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 下载数据信息
 * @author shenguangyang
 * @date 2021-12-04 13:01
 */
@Getter
@ToString
public class VcrDownloadInfo {
    /**
     * 通道号
     */
    private final Integer channel;

    /**
     * 下载视频的起始时间戳, 实际下载的视频是精确到s的
     */
    private final Long startTimestamp;

    /**
     * 下载视频的结束时间戳, 实际下载的视频是精确到s的
     */
    private final Long endTimestamp;

    private final HCNetSDK.NET_DVR_TIME dvrStartTime;

    private final HCNetSDK.NET_DVR_TIME dvrEndTime;

    /**
     * 将视频下载到什么位置
     */
    private final String filePath;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public VcrDownloadInfo(Integer channel, Long startTimestamp, Long endTimestamp, String filePath) {
        Assert.isTrue(startTimestamp != null && startTimestamp > 0 && endTimestamp != null && endTimestamp > 0,
                "startTimestamp != null && startTimestamp > 0 && endTimestamp != null && endTimestamp > 0");
        Assert.isTrue(StringUtils.isNotEmpty(filePath), "filePath not empty");
        Assert.isTrue(channel > 0, "channel > 0");
        Assert.isTrue(endTimestamp > startTimestamp, "endTimestamp > startTimestamp");
        this.channel = channel;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.filePath = filePath;
        dvrStartTime = VcrUtils.getDvrTime(startTimestamp);
        dvrEndTime = VcrUtils.getDvrTime(endTimestamp);
    }

    /**
     * @param startTimeStr 起始时间, 格式yyyy-MM-dd HH:mm:ss
     * @param endTimeStr 结束时间, 格式yyyy-MM-dd HH:mm:ss
     */
    public VcrDownloadInfo(Integer channel, String startTimeStr, String endTimeStr, String filePath) throws ParseException {
        this(channel, sdf.parse(startTimeStr).getTime(), sdf.parse(endTimeStr).getTime(), filePath);
    }
}
