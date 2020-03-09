package com.ldm.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    @Autowired
    private AmqpTemplate amqpTemplate;
    public void send(String message){
        amqpTemplate.convertAndSend(MQConfig.QUEUE,message);
    }
}
