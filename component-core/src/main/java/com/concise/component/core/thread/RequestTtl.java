package com.concise.component.core.thread;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 请求ttl
 * ttl: TransmittableThreadLocal
 * @author shenguangyang
 * @date 2021-10-10 8:53
 */
public class RequestTtl {
    private static final TransmittableThreadLocal<RequestContext> ttl = new TransmittableThreadLocal<>();

    public static RequestContext get() {
        RequestContext requestContext = ttl.get();
        if (requestContext == null) {
            synchronized (RequestTtl.class) {
                requestContext = new RequestContext();
                set(requestContext);
            }
        }
        return requestContext;
    }

    public static void set(RequestContext requestContext) {
        ttl.set(requestContext);
    }

}
