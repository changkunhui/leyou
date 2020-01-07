package com.leyou.sms.listener;

import com.leyou.sms.utils.SmsHelper;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.SMS_VERIFY_CODE_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;
import static com.leyou.sms.constants.SmsConstants.*;

@Component
//rocketMQTemplate.convertAndSend(SMS_TOPIC_NAME+":"+VERIFY_CODE_TAGS,map);
@RocketMQMessageListener(consumerGroup = SMS_VERIFY_CODE_CONSUMER,topic = SMS_TOPIC_NAME,selectorExpression = VERIFY_CODE_TAGS ,messageModel= MessageModel.CLUSTERING)
public class SmsListener implements RocketMQListener<Map> {

    @Autowired
    private SmsHelper smsHelper;

    @Override
    public void onMessage(Map map) {
        String PhoneNumbers = map.get(SMS_PARAM_KEY_PHONE).toString();
        String SignName = map.get(SMS_PARAM_KEY_SIGN_NAME).toString();
        String TemplateCode = map.get(SMS_PARAM_KEY_TEMPLATE_CODE).toString();
        String TemplateParam = map.get(SMS_PARAM_KEY_TEMPLATE_PARAM).toString();
        smsHelper.sendSms(PhoneNumbers,SignName,TemplateCode,TemplateParam);
    }
}
