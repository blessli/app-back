package com.ldm.rabbitmq;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4Fwe6hHrbvykruL6WyMF", "WCcnIqRIKYb2dC8IAmu55tyRxzY7bh");
        IAcsClient client = new DefaultAcsClient(profile);
        String phone="17774621486";
        String code="123456";
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "友约");
        request.putQueryParameter("TemplateCode", "SMS_184631762");
        String key="{\"code\":"+code+"}";
        request.putQueryParameter("TemplateParam",key);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
