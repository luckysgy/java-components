package com.concise.component.core.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.concise.component.core.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: 输出代码执行时间的工具类
 * @date 2020/11/20 13:45
 */
public class CodeRunningTimeUtils {
    private static final Logger log = LoggerFactory.getLogger(CodeRunningTimeUtils.class);
    private final Long startTime;
    private final String className;
    private final String methodName;
    private static final TransmittableThreadLocal<CodeRunningTimeUtils> ttl = new TransmittableThreadLocal<>();

    private CodeRunningTimeUtils(Long startTime, String className, String methodName) {
        this.startTime = startTime;
        this.methodName = methodName;
        this.className = className;
    }

    /**
     * 获取初始时间戳，静态方法
     */
    public static void initRunTimes() {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        // 获取调用者的方法名
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName().substring(stackTraceElement.getClassName().lastIndexOf(".") + 1);
        ttl.set(new CodeRunningTimeUtils(System.currentTimeMillis(), className, methodName));
    }

    /**
     * 获取结束时间戳并打印代码执行时间，静态方法，无参
     */
    public static void printRunTime() {
        printRunTime(null);
    }

    /**
     * 获取结束时间戳并打印代码执行时间，静态方法，有参
     *
     * @param template 字符串会在打印前输出
     */
    public static void printRunTime(CharSequence template, Object... params) {
        if (template != null)
            template = ", " + template;
        else
            template = "";
        String str = StrUtil.format(template, params);
        CodeRunningTimeUtils runningTime = ttl.get();
        if (runningTime != null) {
            long tempTime = System.currentTimeMillis() - runningTime.startTime;
            log.info("{}#{}, time: {} ms{}", runningTime.className, runningTime.methodName, tempTime, str);
        } else {
            throw new BizException("你忘记了放置静态初始方法了");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        CodeRunningTimeUtils.initRunTimes();
        TimeUnit.SECONDS.sleep(1);
        CodeRunningTimeUtils.printRunTime("查询数据: {}", 123123);
    }
}
