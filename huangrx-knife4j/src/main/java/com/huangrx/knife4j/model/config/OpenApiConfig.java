package com.huangrx.knife4j.model.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.huangrx.knife4j.model.enums.ResponseStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * swagger config for open api.
 *
 * @author hh
 */
@Configuration
@EnableKnife4j
public class OpenApiConfig {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("huangrx-knife4j")
                .description("test-knife4j")
                .contact(new Contact("huangrx", "http://www.baidu.com", "2295701930@qq.com"))
                .version("1.0.0")
                .build();
    }

    @Bean
    public Docket AddressApi() {
        return new Docket(DocumentationType.OAS_30)
                //开启个人信息
                .apiInfo(apiInfo())
                .groupName("huangrx-address")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.huangrx.knife4j"))
                .build()
                //每一个请求都可以添加header
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResponse())
                .globalResponses(HttpMethod.POST, getGlobalResponse());

    }

    /**
     * @return global request parameters
     */
    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("Authentication")
                .description("访问令牌")
                .required(false)
                .in(ParameterType.QUERY)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        return parameters;
    }

    /**
     * @return global response code->description
     */
    private List<Response> getGlobalResponse() {
        return ResponseStatus.HTTP_STATUS_ALL.stream().map(
                        a -> new ResponseBuilder().code(a.getResponseCode()).description(a.getDescription()).build())
                .collect(Collectors.toList());
    }

    /**
     * 引入Knife4j提供的扩展类
     */
    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Autowired
    public OpenApiConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }


    @Bean
    public Docket TestApi() {
        return new Docket(DocumentationType.OAS_30)
                //开启个人信息
                .apiInfo(apiInfo())
                .groupName("huangrx-test")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.huangrx.knife4j"))
                .build()
                //每一个请求都可以添加header
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResponse())
                .globalResponses(HttpMethod.POST, getGlobalResponse())
                .extensions(openApiExtensionResolver.buildExtensions("huangrx-test"))
                .extensions(openApiExtensionResolver.buildSettingExtensions());
    }
}
