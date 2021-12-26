package com.concise.demo.hik.vcr;

import com.concise.component.cache.common.service.CacheService;
import com.concise.demo.hik.sdk.HCNetSDK;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 海康录像机 ===> NVR
 * @author shenguangyang
 * @date 2021-12-04 8:45
 */
@Service
public class HikVcr implements Vcr {
    private static final Logger log = LoggerFactory.getLogger(HikVcr.class);
    private static final HCNetSDK hcNetSDK = HCNetSDK.INSTANCE;

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    /**
     * 回放比特率, 如果回放过快可以设置和nvr页面中配置的比特率一样即可 ==> 2048, 默认nvr上
     * 配置的是4096
     */
    private static final Integer PLAY_BITRATE = 2048;

    /**
     * 存储正在回放的map, key: streamId, value: VcrPlayInfo
     */
    private static final Map<String, VcrPlayingInfo> playingInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private CacheService cacheService;
    private final String VCR_PLAYING_INFO_KEY_PRE = "vcrPlayingInfo:";
    // 6个小时
    private final Integer VCR_PLAYING_INFO_KEY_TIMEOUT = 60 * 60 * 6;

    /**
     * 目前SDK私有协议对接时64路以下的NVR的IP通道号是从33开始的，64路以及以上的NVR的IP通道从1开始
     * 请查看 设备网络sdk开发使用手册 ===> 通道和通道号号相关说明
     */
    private static final int START_CHANNEL = 32;
    /**
     * 登录失败
     */
    private static final int LOGIN_FAIL = -1;

    /**
     * 成功登录海康nvr之后返回的userId
     */
    private static Integer userId;

    /**
     * 判断是否登录过
     * @return true登录过
     */
    private boolean isLogin() {
        return userId != null && userId != -1;
    }

    private boolean init() {
        boolean isInitSuccess = hcNetSDK.NET_DVR_Init();
        if (!isInitSuccess) {
            log.error("HcNetSDK init fail, error code: {}, view documents about NET_DVR_GetLastError", hcNetSDK.NET_DVR_GetLastError());
            return false;
        }
        return true;
    }

    @Override
    public boolean login(VcrLoginInfo vcrLoginInfo) {
        if (!init()) {
            return false;
        }
        // 设置连接超时时间
        hcNetSDK.NET_DVR_SetConnectTime(10000, 5);

        if (isLogin()) {
            log.warn("用户 [{}] 已登录过!", vcrLoginInfo.getUsername());
            return true;
        }
        if (vcrLoginInfo.getPort() > 32767) {
            log.warn("the device port number ranges from 0 to 32767");
            return false;
        }
        HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        int loginUserId = hcNetSDK.NET_DVR_Login_V30(
                vcrLoginInfo.getIp(), (short) vcrLoginInfo.getPort(), vcrLoginInfo.getUsername(), vcrLoginInfo.getPassword(), deviceInfo);
        if (loginUserId == LOGIN_FAIL) {
            log.error("failed to login to the device, error code: {}, view documents about NET_DVR_GetLastError", hcNetSDK.NET_DVR_GetLastError());
            return false;
        }
        log.info("login success! byDVRType: {}, byStartDChan: {}, byChanNum: {}, byHighDChanNum: {}, view documents about NET_DVR_DEVICEINFO_V30",
                deviceInfo.byDVRType, deviceInfo.byStartDChan, deviceInfo.byChanNum, deviceInfo.byHighDChanNum);
        userId = loginUserId;
        return true;
    }

    @Override
    public boolean logout() {
        if (!init()) {
            return false;
        }

        if (userId != LOGIN_FAIL) {
            boolean isLogoutSuccess = hcNetSDK.NET_DVR_Logout(userId);
            if (!isLogoutSuccess) {
                log.error("logout fail, error code: {}, view documents about NET_DVR_Logout", hcNetSDK.NET_DVR_GetLastError());
                return false;
            }
        }
        log.info("logout success! ");
        return true;
    }

    @Override
    public boolean download(VcrDownloadInfo info) {
        if (!init() && !isLogin()) {
            return false;
        }

        // 下载句柄
        int downloadHandler = 0;
        try {
            downloadHandler = hcNetSDK.NET_DVR_GetFileByTime(userId, info.getChannel() + START_CHANNEL, info.getDvrStartTime(), info.getDvrEndTime(), info.getFilePath());
            if (downloadHandler == -1) {
                log.error("failed to initialize play handle, error code: {}, view documents about NET_DVR_GetFileByTime", hcNetSDK.NET_DVR_GetLastError());
                return false;
            }

            boolean pbcStart = hcNetSDK.NET_DVR_PlayBackControl(downloadHandler, hcNetSDK.NET_DVR_PLAYSTART, 0, null);
            if (!pbcStart) {
                log.error("NET_DVR_PlayBackControl failed, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                return false;
            }

            IntByReference pos = new IntByReference();
            int tmp = -1;
            while (true) {
                boolean pbcGetPos = hcNetSDK.NET_DVR_PlayBackControl(downloadHandler, hcNetSDK.NET_DVR_PLAYGETPOS, 0, pos);
                if (!pbcGetPos) {
                    break;
                }
                int produce = pos.getValue();
                if ((produce % 10 == 0) && tmp != produce) {
                    tmp = produce;
                    log.info("download produce: {} %", produce);
                }

                if (produce == 100) {
                    break;
                }

                if (produce > 100) {
                    log.error("download failed, error code: {}, view documents about NET_DVR_PlayBackControl", hcNetSDK.NET_DVR_GetLastError());
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hcNetSDK.NET_DVR_StopGetFile(downloadHandler);
            hcNetSDK.NET_DVR_Cleanup();
        }

        log.info("download success! filePath: {}", info.getFilePath());
        return true;
    }

    private String getVcrPlayingInfoCacheKey(String streamId) {
        return VCR_PLAYING_INFO_KEY_PRE + streamId;
    }

    @Override
    public VcrPlayingInfo playStart(VcrPlayInfo playInfo, HCNetSDK.FPlayDataCallBack fPlayDataCallBack, PlayStatusCallBack playStatusCallBack) {
        if (!init() && !isLogin()) {
            return null;
        }
        AtomicReference<VcrPlayingInfo> playingInfoAr = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        asyncExecutor.execute(() -> {
            playStatusCallBack.invoke(new PlayInfo(playInfo.getStreamId(), PlayCode.PLAYING));
            // 回放句柄
            int playHandle = -1;
            try {
                playHandle = hcNetSDK.NET_DVR_PlayBackByTime(
                        userId, playInfo.getChannel() + START_CHANNEL, playInfo.getDvrStartTime(), playInfo.getDvrEndTime(), null);
                if (playHandle == -1) {
                    log.error("NET_DVR_PlayBackByTime fail, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                    return;
                }
                boolean isSetCallBackSuccess = hcNetSDK.NET_DVR_SetPlayDataCallBack(playHandle, fPlayDataCallBack, userId);
                if (!isSetCallBackSuccess) {
                    log.error("NET_DVR_SetPlayDataCallBack fail, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                    return;
                }

                // 控制回放状态开始回放
                boolean pbcStart = hcNetSDK.NET_DVR_PlayBackControl(playHandle, hcNetSDK.NET_DVR_PLAYSTART, 0, null);
                if (!pbcStart) {
                    log.error("NET_DVR_PlayBackControl failed, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                    return;
                }
                int finalPlayHandle = playHandle;

                // 这里延时设置码流目的是, 开始让sdk回调速度快一些, 加快播放速度
                asyncExecutor.execute(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        // 控制历史回放拉流以及推流时的速度和直播一致
                        boolean pdcSpeed = hcNetSDK.NET_DVR_PlayBackControl(finalPlayHandle, hcNetSDK.NET_DVR_SETSPEED, PLAY_BITRATE, null);
                        if (!pdcSpeed) {
                            log.error("NET_DVR_PlayBackControl failed, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                        }
                    } catch (InterruptedException e) {
                        log.error("NET_DVR_PlayBackControl exception, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                    }
                });

                VcrPlayingInfo playingInfo = new VcrPlayingInfo(playInfo.getStreamId(), playInfo, playHandle);
                cacheService.opsValue().setEx(getVcrPlayingInfoCacheKey(playingInfo.getStreamId()), playingInfo, VCR_PLAYING_INFO_KEY_TIMEOUT);
                playingInfoAr.set(playingInfo);
                countDownLatch.countDown();

                IntByReference pos = new IntByReference();
                int tmp = -1;
                PlayInfo play = null;
                while (true) {
                    boolean pbcGetPos = hcNetSDK.NET_DVR_PlayBackControl(playHandle, hcNetSDK.NET_DVR_PLAYGETPOS, 0, pos);
                    if (!pbcGetPos) {
                        log.info("play stop");
                        play = new PlayInfo(playInfo.getStreamId(), PlayCode.STOP);
                        break;
                    }
                    int produce = pos.getValue();
                    if ((produce % 10 == 0) && tmp != produce) {
                        tmp = produce;
                        log.info("play produce: {} %", produce);
                    }

                    if (produce == 100) {
                        log.info("play end");
                        play = new PlayInfo(playInfo.getStreamId(), PlayCode.STOP);
                        break;
                    }

                    if (produce > 100) {
                        log.error("play failed, error code: {}, view documents about NET_DVR_PlayBackControl", hcNetSDK.NET_DVR_GetLastError());
                        play = new PlayInfo(playInfo.getStreamId(), PlayCode.ERROR);
                        break;
                    }
                }
                playStatusCallBack.invoke(play);
            } catch (Exception e) {
                log.error("startPlay fail, error code: {}", hcNetSDK.NET_DVR_GetLastError());
                countDownLatch.countDown();
                playStatusCallBack.invoke(new PlayInfo(playInfo.getStreamId(), PlayCode.ERROR));
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        return playingInfoAr.get();
    }

    @Override
    public boolean playStop(String streamId) {
        Object cacheObj = cacheService.opsValue().get(getVcrPlayingInfoCacheKey(streamId));
        if (cacheObj == null) {
            log.warn("no playback or stopped, streamId: {}", streamId);
            return false;
        }
        VcrPlayingInfo vcrPlayingInfo = (VcrPlayingInfo) cacheObj;
        Integer playHandle = vcrPlayingInfo.getPlayHandle();
        boolean stopPlayBack = hcNetSDK.NET_DVR_StopPlayBack(playHandle);
        if (!stopPlayBack) {
            log.error("NET_DVR_StopPlayBack fail, error code: {}", hcNetSDK.NET_DVR_GetLastError());
            return false;
        }
        return true;
    }

}
