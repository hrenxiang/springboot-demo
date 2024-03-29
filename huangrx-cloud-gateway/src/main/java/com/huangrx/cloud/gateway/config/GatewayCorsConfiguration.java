package com.huangrx.cloud.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置，nacos中也可配置
 *
 * @author hrenxiang
 * @since 2022-10-17 17:55:49
 */
@Configuration
public class GatewayCorsConfiguration {
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//
//        // 初始化CORS配置对象
//        CorsConfiguration config = new CorsConfiguration();
//        // 允许的域,不要写*，否则cookie就无法使用了
//        config.addAllowedOrigin("http://manager.gmall.com");
//        config.addAllowedOrigin("http://www.gmall.com");
//        config.addAllowedOrigin("http://static.gmall.com");
//        config.addAllowedOrigin("http://item.gmall.com");
//        // 允许的头信息
//        config.addAllowedHeader("*");
//        // 允许的请求方式
//        config.addAllowedMethod("*");
//        // 是否允许携带Cookie信息
//        config.setAllowCredentials(true);
//
//        // 添加映射路径，我们拦截一切请求
//        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        corsConfigurationSource.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(corsConfigurationSource);
//    }
}