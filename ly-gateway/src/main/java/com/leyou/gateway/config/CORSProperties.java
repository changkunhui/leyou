package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author changkunhui
 * @date 2019/12/24 17:46
 */

@Data
@ConfigurationProperties(prefix = "ly.cors")
public class CORSProperties {

    private List<String> allowedOrigins;
    private Boolean allowedCredentials;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private Long maxAge;
    private String filterPath;
}
