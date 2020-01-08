package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author changkunhui
 * @date 2020/1/8 13:04
 */


@SpringBootApplication
@EnableConfigServer
public class LyConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyConfigServerApplication.class,args);
    }
}
