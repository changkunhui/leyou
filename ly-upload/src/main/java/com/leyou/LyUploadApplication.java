package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author changkunhui
 * @date 2019/12/25 15:02
 */


@SpringBootApplication
@EnableDiscoveryClient
public class LyUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyUploadApplication.class,args);
    }

}
