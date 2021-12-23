package com.concise.component.feign.config;

import com.concise.component.feign.domain.ResultStatusDecoder;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenguangyang
 * @date 2021-05-29 18:34
 */
@Configuration
public class FeignAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FeignAutoConfiguration.class);

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Decoder feignDecoder() {
        return new ResultStatusDecoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters))));
    }

    /**
     * 自定义重试机制
     */
    @Bean
    public Retryer feignRetryer() {
        // fegin提供的默认实现，最大请求次数为5，初始间隔时间为100ms，下次间隔时间1.5倍递增，
        // 重试间最大间隔时间为1s，
        return new Retryer.Default();
    }

    @Bean
    public ErrorDecoder feignError() {
        return (key, response) -> {
            if (response.status() == 400) {
                log.error("请求服务400参数错误,返回:{}", response.body());
            }

            if (response.status() == 409) {
                log.error("请求服务409异常,返回:{}", response.body());
            }

            if (response.status() == 404) {
                log.error("请求服务404异常,返回:{}", response.body());
            }

            // 其他异常交给Default去解码处理
            // 这里使用单例即可，Default不用每次都去new
            return new ErrorDecoder.Default().decode(key, response);
        };
    }
}
