package com.concise.component.knife4j.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author shenguangyang
 * @date 2021/03/09
 */
@Configuration
@EnableOpenApi
@EnableKnife4j
public class Knife4jConfig {
    @Value("${knife4j.enable:false}")
    private Boolean enable;

    @Resource
    private Knife4jApiInfoProperties knife4jApiInfoProperties;

    @Bean //配置docket以配置Swagger具体参数
    public Docket docket(Environment environment) {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                // .globalRequestParameters(getGlobalRequestParameters())
                // 添加全局响应状态码
                .globalResponses(HttpMethod.GET, getGlobalResponseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResponseMessage())
                .globalResponses(HttpMethod.DELETE, getGlobalResponseMessage())
                .globalResponses(HttpMethod.PUT, getGlobalResponseMessage())

                // 配置是否启用Swagger，如果是false，在浏览器将无法访问
                .enable(enable)
                // 以下是一组的
                // 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                // 配置如何通过path过滤,即这里只扫描请求以/api开头的接口
                // .paths(PathSelectors.ant("/api/**"))
                .build();
    }
    /**
     * 项目信息
     */
    private ApiInfo apiInfo() {
        Knife4jApiInfoProperties.Contact contact = knife4jApiInfoProperties.getContact();
        return new ApiInfoBuilder().title(knife4jApiInfoProperties.getTitle())
                .description(knife4jApiInfoProperties.getDescription())
                .contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail()))
                .version(knife4jApiInfoProperties.getVersion())
                .build();
    }

    /**
     * 生成全局通用参数
     *
     * @return
     */
    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("x-access-token")
                .description("令牌")
                .required(false)
                .in(ParameterType.HEADER)
                .build());
        parameters.add(new RequestParameterBuilder()
                .name("Equipment-Type")
                .description("产品类型")
                .required(false)
                .in(ParameterType.HEADER)
                .build());
        return parameters;
    }

    /**
     * 生成通用响应信息
     *
     */
    private List<Response> getGlobalResponseMessage() {
        //添加全局响应状态码
//        List<Response> responseMessageList = new ArrayList<>();
//        Arrays.stream(ResultCode.values()).forEach(resultEnum -> {
//            responseMessageList.add(
//                    new ResponseBuilder()
//                            .code(String.valueOf(resultEnum.getCode()))
//                            .description(resultEnum.getMsg())
//                            .build()
//            );
//        });
//        return responseMessageList;
        return new ArrayList<>();
    }
}
