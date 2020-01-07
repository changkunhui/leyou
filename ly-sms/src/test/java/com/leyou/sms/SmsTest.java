package com.leyou.sms;


import com.leyou.sms.utils.SmsHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;
import static com.leyou.sms.constants.SmsConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {

    @Autowired
   private SmsHelper smsHelper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void sendSmsTest(){
//        String PhoneNumbers,String SignName,String TemplateCode,String  TemplateParam
        String numeric = RandomStringUtils.randomNumeric(4);
        System.out.println(numeric);
        String param = "{\"code\":\""+numeric+"\"}";
        smsHelper.sendSms("18238761209","快乐优雅购物","SMS_173479212",param);

    }


    @Test
    public void sendSms(){
//        public static final String SMS_PARAM_KEY_PHONE = "PhoneNumbers";
//        public static final String SMS_PARAM_KEY_SIGN_NAME = "SignName";
//        public static final String SMS_PARAM_KEY_TEMPLATE_CODE = "TemplateCode";
//        public static final String SMS_PARAM_KEY_TEMPLATE_PARAM= "TemplateParam";

        //RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();

        String numeric = RandomStringUtils.randomNumeric(4);
        System.out.println(numeric);
        String param = "{\"code\":\""+numeric+"\"}";

        Map<String,String> map = new HashMap<>();
        map.put(SMS_PARAM_KEY_PHONE,"18238761209");
        map.put(SMS_PARAM_KEY_SIGN_NAME,"乐优商城");
        map.put(SMS_PARAM_KEY_TEMPLATE_CODE,"SMS_181857033");
        map.put(SMS_PARAM_KEY_TEMPLATE_PARAM,param);
        rocketMQTemplate.convertAndSend(SMS_TOPIC_NAME+":"+VERIFY_CODE_TAGS,map);
    }


}
