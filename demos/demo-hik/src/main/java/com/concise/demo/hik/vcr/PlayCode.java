package com.concise.demo.hik.vcr;

import lombok.Getter;

/**
 * @author shenguangyang
 * @date 2021-12-05 15:55
 */
@Getter
public enum PlayCode {
    ERROR(-1, "播放异常"),
    PLAYING(1, "播放中"),
    STOP(2, "播放已停止");

    private final Integer code;
    private final String message;

    PlayCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
