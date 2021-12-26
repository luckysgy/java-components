package com.concise.demo.hik.service;

import com.concise.demo.hik.sdk.HCNetSDK;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.concise.demo.hik.sdk.HCNetSDK.COMM_UPLOAD_PLATE_RESULT;

/**
 * @author shenguangyang
 * @date 2021-12-26 10:39
 */
public class HikVisionService {
    private static final Logger log = LoggerFactory.getLogger(HikVisionService.class);

    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    static String m_sUsername = "admin";//设备用户名
    static String m_sPassword = "abc12345";//设备密码
    static short m_sPort = 8000;//端口号，这是默认的
    public NativeLong lUserID;//用户句柄
    public int lAlarmHandle;//报警布防句柄
    public int lListenHandle;//报警监听句柄
    public NativeLong RemoteConfig;
    public static int code = 5;

    //撤防
    public void CloseAlarmChan() {
        //报警撤防
        if (lAlarmHandle > -1) {
            if (hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
                System.out.println("撤防成功");
                lAlarmHandle = -1;
            }
        }
    }

    public  void initMemberFlowUpload(String m_sDeviceIP, int remainMinuteTime) throws InterruptedException {
        // 初始化
        Boolean flag = hCNetSDK.NET_DVR_Init();
        if (flag){
            System.out.println("初始化成功");
        }else{
            System.out.println("初始化失败");
        }
        //设置连接时间与重连时间
        hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
        hCNetSDK.NET_DVR_SetReconnect(100000, true);
        //设备信息, 输出参数
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
        // 注册设备-登录参数，包括设备地址、登录用户、密码等
        m_strLoginInfo.sDeviceAddress = new byte[hCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());
        m_strLoginInfo.sUserName = new byte[hCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());
        m_strLoginInfo.sPassword = new byte[hCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());
        m_strLoginInfo.wPort = m_sPort;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.write();

        //设备信息, 输出参数
        int lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo,m_strDeviceInfo);
        if(lUserID< 0){
            System.out.println("hCNetSDK.NET_DVR_Login_V30()"+"\n" +hCNetSDK.NET_DVR_GetErrorMsg(null));
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }
        //设置报警回调函数
        if (hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(new HikVisionService().new FMSGCallBack(), null)) {
            System.out.println("设置回调函数成功");
        } else {
            System.out.println("设置回调函数失败"+hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        //启用布防
        HCNetSDK.NET_DVR_SETUPALARM_PARAM lpSetupParam = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        lpSetupParam.dwSize = 0;
        lpSetupParam.byLevel = 1;//布防优先级：0- 一等级（高），1- 二等级（中）
        lpSetupParam.byAlarmInfoType = 1;//上传报警信息类型: 0- 老报警信息(NET_DVR_PLATE_RESULT), 1- 新报警信息(NET_ITS_PLATE_RESULT)
        int lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID,lpSetupParam);
        if (lAlarmHandle< 0)
        {
            System.out.println("NET_DVR_SetupAlarmChan_V41 error, %d\n"+hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }
        System.out.println("布防成功,开始监测车辆");

        //启动监听----------------------------------------------
        int iListenPort = 8000;
        String m_sListenIP = "192.168.190.70";

        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(m_sListenIP, (short) iListenPort,  new HikVisionService().new FMSGCallBack(), null);
        if(lListenHandle < 0) {
//            JOptionPane.showMessageDialog(null, "启动监听失败，错误号:" +  hCNetSDK.NET_DVR_GetLastError());
        }
        else {
            System.out.println("启动监听成功");
        }


    }
    public class FMSGCallBack implements HCNetSDK.FMSGCallBack{

        @Override
        public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            System.out.println("〈－－进入回调,开始识别车牌－－〉");
            try {
                String sAlarmType = new String();
                String[] newRow = new String[3];
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                String[] sIP = new String[2];
                switch (lCommand) {
                    case COMM_UPLOAD_PLATE_RESULT://COMM_UPLOAD_PLATE_RESULT:
                        HCNetSDK.NET_DVR_PLATE_RESULT strPlateResult = new HCNetSDK.NET_DVR_PLATE_RESULT();
                        strPlateResult.write();
                        Pointer pPlateInfo = strPlateResult.getPointer();

                        //pAlarmInfo.getByteArray(0, strPlateResult.size())
                        pPlateInfo.write(0, pAlarmInfo.getByteArray(0, strPlateResult.size()), 0, strPlateResult.size());
                        strPlateResult.read();
                        try {
                            String srt3 = new String(strPlateResult.struPlateInfo.sLicense, "GBK");
                            sAlarmType = sAlarmType + "：交通抓拍上传，车牌：" + srt3;
                        } catch (UnsupportedEncodingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        newRow[0] = dateFormat.format(new Date());
                        //报警类型
                        newRow[1] = sAlarmType;
                        //报警设备IP地址
                        sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                        newRow[2] = sIP[0];
//                    alarmTableModel.insertRow(0, newRow);
                        log.info(strPlateResult.byResultType + "<-识别类型 ->" +
                                strPlateResult.dwCarPicLen + "原图<-图片长度-><-近景图->" + strPlateResult.dwPicLen);
                        break;
                    case 0x3050:    //交通抓拍的终端图片上传
                        HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
                        strItsPlateResult.write();
                        Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                        pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                        strItsPlateResult.read();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
