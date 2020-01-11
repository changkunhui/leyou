package com.leyou.order.listener;

import com.leyou.order.service.TbOrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.ORDER_OVERTIME_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ORDER_OVERTIME_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ORDER_TOPIC_NAME;

/**
 * 监听,清理一个小时未支付的订单
 * @author changkunhui
 * @date 2020/1/11 16:06
 */

@Component
//              rocketMQTemplate.convertAndSend(ORDER_TOPIC_NAME+":"+ORDER_OVERTIME_TAGS,"开始清理")
@RocketMQMessageListener(consumerGroup=ORDER_OVERTIME_CONSUMER,topic = ORDER_TOPIC_NAME,selectorExpression=ORDER_OVERTIME_TAGS)
public class OrderCleanListener implements RocketMQListener<String> {

    @Autowired
    private TbOrderService orderService;

    @Override
    public void onMessage(String s) {
        orderService.cleanOverTimeOrder();//调用service执行清理
    }
}
