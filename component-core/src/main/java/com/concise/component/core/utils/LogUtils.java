package com.concise.component.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 日志工具
 * @author shenguangyang
 * @date 2021-09-09 6:33
 */
public class LogUtils {
    /**
     *
     * 在日志文件中，打印异常堆栈
     * @param e
     * @return String
     */
    public static String logExceptionStack(Throwable e) {
        StringWriter errorsWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(errorsWriter));
        return e.getMessage() + "\n" + errorsWriter;
    }
}
