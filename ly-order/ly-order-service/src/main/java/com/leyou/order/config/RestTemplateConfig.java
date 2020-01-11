package com.leyou.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * @author changkunhui
 * @date 2020/1/11 13:33
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        StringHttpMessageConverter messageConverter = new StringHttpMessageConverter();
        messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
        restTemplate.getMessageConverters().set(1,messageConverter);
        return restTemplate;
    }
}
