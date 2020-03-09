package com.ldm.rabbitmq;

import com.ldm.config.MQConfig;
import com.ldm.entity.activity.ActivityDetail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        ActivityDetail activityDetail=MQUtil.stringToBean(message,ActivityDetail.class);
//        System.out.println(activityDetail.toString());
//    }
}
