package com.concise.demo.hik.vcr;

import com.concise.component.cache.common.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 视频播放回调
 * @author shenguangyang
 * @date 2021-12-05 15:59
 */
@Service
public class VideoPlayStatusCallBack implements PlayStatusCallBack {
    @Autowired
    private CacheService cacheService;

    private final String keyPre = "play_status:";

    /**
     * 状态过期时间 120s
     */
    private static final Integer STATUS_TIMEOUT = 120;

    @Override
    public void invoke(PlayInfo playInfo) {
        cacheService.opsValue().setEx(keyPre + playInfo.getStreamId(), playInfo, STATUS_TIMEOUT);
    }

    @Override
    public PlayInfo getPlayInfo(String streamId) {
        Object object = cacheService.opsValue().get(keyPre + streamId);
        return object != null ? (PlayInfo) object : new PlayInfo(streamId, PlayCode.STOP);
    }
}
