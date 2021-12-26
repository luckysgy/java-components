package com.concise.demo.hik.vcr;

/**
 * 播放状态回调
 * @author shenguangyang
 * @date 2021-12-05 14:36
 */
public interface PlayStatusCallBack {
    void invoke(PlayInfo playInfo);
    PlayInfo getPlayInfo(String streamId);
}
