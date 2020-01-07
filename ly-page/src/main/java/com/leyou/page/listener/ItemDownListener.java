package com.leyou.page.listener;

import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.ITEM_PAGE_DOWN_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * @author changkunhui
 * @date 2020/1/3 15:57
 */

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup=ITEM_PAGE_DOWN_CONSUMER,topic = ITEM_TOPIC_NAME,selectorExpression=ITEM_DOWN_TAGS)
public class ItemDownListener implements RocketMQListener<Long> {

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Long spuId) {
        log.info("[静态页服务]- (商品下架) - 接收消息，spuId={}", spuId);
        //商品下架,删除静态页面
        pageService.removeItemPage(spuId);
    }
}
