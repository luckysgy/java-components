package com.concise.demo.hik.vcr;

import com.concise.demo.hik.sdk.HCNetSDK;

import java.util.Calendar;
import java.util.Date;

/**
 * @author shenguangyang
 * @date 2021-12-04 16:07
 */
public class VcrUtils {
    /**
     * @param dataTimestamp 时间字符串格式
     * @return 返回nvr时间, 精确到s
     **/
    public static HCNetSDK.NET_DVR_TIME getDvrTime(long dataTimestamp) {
        HCNetSDK.NET_DVR_TIME time = new HCNetSDK.NET_DVR_TIME();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(dataTimestamp));
        time.dwYear = cal.get(Calendar.YEAR);
        time.dwMonth = cal.get(Calendar.MONTH) + 1;
        time.dwDay = cal.get(Calendar.DAY_OF_MONTH);
        time.dwHour = cal.get(Calendar.HOUR);
        time.dwMinute = cal.get(Calendar.MINUTE);
        time.dwSecond = cal.get(Calendar.SECOND);
        return time;
    }
}
