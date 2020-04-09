package com.ldm.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 阿里云发送短信验证码
     * @param phone
     */
    public void sendSMS(String phone){
        amqpTemplate.convertAndSend(MQConfig.SMS_QUEUE,phone);
    }

    /**
     * feed流之动态发布处理
     * @param message
     */
    public void feedDynamicPublish(String message){
        amqpTemplate.convertAndSend(MQConfig.Feed_Dynamic_Publish_QUEUE,message);
    }
}
