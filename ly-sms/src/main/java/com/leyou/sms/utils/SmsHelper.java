package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.sms.constants.SmsConstants.OK;
import static com.leyou.sms.constants.SmsConstants.SMS_RESPONSE_KEY_MESSAGE;

@Component
@Slf4j
public class SmsHelper {

    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private  IAcsClient client;
    public void sendSms(String PhoneNumbers,String SignName,String TemplateCode,String  TemplateParam) {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(smsProperties.getDomain());
        request.setVersion(smsProperties.getVersion());
        request.setAction(smsProperties.getAction());
        request.putQueryParameter("RegionId", smsProperties.getRegionID());
        request.putQueryParameter("PhoneNumbers", PhoneNumbers);
        request.putQueryParameter("SignName",  SignName);
        request.putQueryParameter("TemplateCode", TemplateCode);
        request.putQueryParameter("TemplateParam", TemplateParam);
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String, String> resp = JsonUtils.toMap(response.getData(), String.class, String.class);
            String statusMessage = resp.get(SMS_RESPONSE_KEY_MESSAGE);
            if(!StringUtils.equals(statusMessage,OK)){
                log.error("【SMS服务】发送短信失败，原因{}", resp.get(SMS_RESPONSE_KEY_MESSAGE));
            }
        } catch (ServerException e) {
            log.error("发送失败....服务器异常");
        } catch (ClientException e) {
            log.error("发送失败....客户端异常");
        }
    }
}
