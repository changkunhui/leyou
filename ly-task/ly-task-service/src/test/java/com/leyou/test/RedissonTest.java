package com.leyou.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author changkunhui
 * @date 2020/1/11 17:54
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedissonTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void hello() throws InterruptedException {

        //创建所对象
        RLock taskLock = redissonClient.getLock("taskLock");

        //获取锁,并设置失效时间为50秒
        boolean isLock = taskLock.tryLock(50, TimeUnit.SECONDS);

        // 判断是否获取锁
        if (!isLock) {
            // 获取失败
            log.info("获取锁失败，停止定时任务");
            return;
        }
        try {
            // 执行业务
            log.info("获取锁成功，执行定时任务。");
            // 模拟任务耗时
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("任务执行异常", e);
        } finally {
            // 释放锁
            taskLock.unlock();
            log.info("任务执行完毕，释放锁");
        }

    }

}
