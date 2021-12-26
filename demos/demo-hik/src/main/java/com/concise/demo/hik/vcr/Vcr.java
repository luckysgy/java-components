package com.concise.demo.hik.vcr;


import com.concise.demo.hik.sdk.HCNetSDK;

/**
 * @author shenguangyang
 * @date 2021-12-04 8:49
 */
public interface Vcr {
    /**
     * 登录
     * @param vcrLoginInfo 登录信息
     * @return true登录成功
     */
    boolean login(VcrLoginInfo vcrLoginInfo);

    /**
     * 退出设备
     * @return true退出成功
     */
    boolean logout();

    /**
     * 下载视频
     * @param vcrDownloadInfo 下载信息
     * @return true下载成功
     */
    boolean download(VcrDownloadInfo vcrDownloadInfo);

    /**
     * 启动回放, 注意startPlay所在线程不能中断或退出, 否则sdk回调将会中断
     * @param vcrPlayInfo 播放信息
     * @param fPlayDataCallBack 回调函数
     * @param playStatusCallBack 播放状态回调
     * @return 正在回放的流信息, null 表示回放失败
     */
    VcrPlayingInfo playStart(VcrPlayInfo vcrPlayInfo, HCNetSDK.FPlayDataCallBack fPlayDataCallBack, PlayStatusCallBack playStatusCallBack);

    /**
     * 停止回放
     * @param streamId 流id
     */
    boolean playStop(String streamId);
}
