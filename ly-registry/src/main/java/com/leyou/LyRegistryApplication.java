package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author changkunhui
 * @date 2019/12/22 13:12
 */

@SpringBootApplication
@EnableEurekaServer     //开启注册中心服务
public class LyRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyRegistryApplication.class,args);
    }

}
