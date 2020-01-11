package com.leyou.task.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ORDER_OVERTIME_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ORDER_TOPIC_NAME;

/**
 * 清理未支付订单的定时任务
 * @author changkunhui
 * @date 2020/1/11 15:59
 */

@Component
@Slf4j
public class CleanOrderTask {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0/1 * * * ?")//每个一个小时执行一次
    public void sendCleanOrderTask(){

        RLock rLock = redissonClient.getLock("overTimeTask");

        boolean tryLock = rLock.tryLock();
        if(!tryLock){//判断是否上锁（是否获取锁成功）
            log.debug("获取锁失败");
            return;
        }

        try{
            rocketMQTemplate.convertAndSend(ORDER_TOPIC_NAME+":"+ORDER_OVERTIME_TAGS,"开始清理");//这个地方随便放
            System.out.println("清理任务已经发出");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();//释放锁
            log.debug("释放锁");
        }

    }
}
