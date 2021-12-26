package com.concise.demo.hik.vcr;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 登录vcr(录像机) 信息
 * @author shenguangyang
 * @date 2021-12-04 8:51
 */
@Getter
@ToString
public class VcrLoginInfo implements Serializable {
    /**
     * 设备ip
     */
    private final String ip;
    /**
     * 设备端口号
     */
    private final int port;
    /**
     * 设备用户名
     */
    private final String username;
    /**
     * 设备密码
     */
    private final String password;

    public VcrLoginInfo(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
