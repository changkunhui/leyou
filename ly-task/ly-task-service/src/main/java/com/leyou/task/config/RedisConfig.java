package com.leyou.task.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient(RedisProperties prop) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+prop.getHost()+":"+prop.getPort());
        return Redisson.create(config);
    }
}

