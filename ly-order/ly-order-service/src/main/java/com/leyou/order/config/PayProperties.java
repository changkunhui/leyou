package com.leyou.order.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ly.pay.wx")
public class PayProperties {
    private String appID;
    private String mchID;
    private String key;
    private String notifyurl;
}