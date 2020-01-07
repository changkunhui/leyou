package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author changkunhui
 * @date 2019/12/22 13:24
 */


@SpringCloudApplication
@EnableZuulProxy
public class LyGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyGatewayApplication.class,args);
    }

}
