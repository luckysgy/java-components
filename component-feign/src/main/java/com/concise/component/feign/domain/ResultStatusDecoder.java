package com.concise.component.feign.domain;

import feign.Response;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * feign返回值拦截
 *
 * feign Response只能读一次问题 和 feign response 返回值拦截
 *
 * spring cloud 对feign调用对返回值做了包装处理，通过一些列Decoder来处理feign访问的返回值。
 * 具体流程 从SynchronousMethodHandler中的decoder开始会经历如下几个decoder：
 * OptionalDecoder -> ResponseEntityDecoder -> SpringDecoder
 *
 * feign返回值拦截只需要自定义一个coder，替换 FeignClientsConfiguration 中的 decoder定义即可，
 *
 * 由于response中的body只能读取一次，所以最后需要把body回写，再重新生成response传递到下一个decoder
 * @author shenguangyang
 * @date 2021-05-29 18:33
 */
public class ResultStatusDecoder implements Decoder {
    private static final Logger log = LoggerFactory.getLogger(ResultStatusDecoder.class);

    public static final String CONTENT_KEY = "content";
    final Decoder delegate;

    public ResultStatusDecoder(Decoder delegate) {
        Objects.requireNonNull(delegate, "Decoder must not be null. ");
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        // 判断是否返回参数是否是异常
        String resultStr = IOUtils.toString(response.body().asInputStream(), String.valueOf(StandardCharsets.UTF_8));
        // 拿到返回值，进行自定义逻辑处理
        log.debug("do business ,result msg ->{}",resultStr);
        // 回写body,因为response的流数据只能读一次，这里回写后重新生成response
        return delegate.decode(response.toBuilder().body(resultStr, StandardCharsets.UTF_8).build(), type);
    }
}

