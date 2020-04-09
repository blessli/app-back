package com.ldm.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static final String SMS_QUEUE = "SMS";
    // 动态发布处理队列
    public static final String Feed_Dynamic_Publish_QUEUE="feedDynamicPublishQueue";
    // 关注取关处理队列
    public static final String Feed_Follow_QUEUE="feedFollowQueue";
    /**
     * Direct 模式
     *
     */

    @Bean
    public Queue queue() {
        return new Queue(MQConfig.SMS_QUEUE, true);
    }
    @Bean
    public Queue queue1(){
        return new Queue(MQConfig.Feed_Dynamic_Publish_QUEUE, true);
    }
    @Bean
    public Queue queue2(){
        return new Queue(MQConfig.Feed_Follow_QUEUE, true);
    }
}

