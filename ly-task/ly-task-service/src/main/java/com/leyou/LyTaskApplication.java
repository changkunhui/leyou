package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author changkunhui
 * @date 2020/1/11 15:56
 */

@SpringBootApplication
@EnableScheduling
public class LyTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyTaskApplication.class,args);
    }
}
