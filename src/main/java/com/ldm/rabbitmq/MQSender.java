package com.ldm.rabbitmq;

import com.ldm.config.MQConfig;
import com.ldm.entity.activity.ActivityDetail;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
//    @Autowired
//    private AmqpTemplate amqpTemplate;
//    public void send(ActivityDetail activityDetail){
//        String message=MQUtil.beanToString(activityDetail);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,message);
//    }
}
