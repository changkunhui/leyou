package com.leyou.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(CORSProperties.class)
public class GlobalCORSConfig {
    @Bean
    public CorsFilter corsFilter(CORSProperties properties) {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        //config.addAllowedOrigin("http://manage.leyou.com");
        //config.addAllowedOrigin("http://www.leyou.com");
        properties.getAllowedOrigins().forEach(config::addAllowedOrigin);

        //2) 是否发送Cookie信息
        //config.setAllowCredentials(true);
        config.setAllowCredentials(properties.getAllowedCredentials());

        //3) 允许的请求方式
        /*config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");*/

        properties.getAllowedMethods().forEach(config::addAllowedMethod);

        // 4）允许的头信息
        //config.addAllowedHeader("*");
        properties.getAllowedHeaders().forEach(config::addAllowedHeader);

        // 5）有效期 单位秒
        //config.setMaxAge(3600L);
        config.setMaxAge(properties.getMaxAge());

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(properties.getFilterPath(), config);
        //3.返回新的CORSFilter
        return new CorsFilter(configSource);
    }
}