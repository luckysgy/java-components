package com.concise.component.core.thread;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 应用的ttl
 * ttl: TransmittableThreadLocal
 * @author shenguangyang
 * @date 2021-10-10 8:53
 */
public class AppTtl {
    private static final TransmittableThreadLocal<AppContext> appContextTtl = new TransmittableThreadLocal<>();

    public static AppContext getAppContext() {
        AppContext appContext = appContextTtl.get();
        if (appContext == null) {
            synchronized (AppTtl.class) {
                appContext = new AppContext();
                setAppContext(appContext);
            }
        }
        return appContext;
    }

    public static void setAppContext(AppContext appContext) {
        appContextTtl.set(appContext);
    }

}
