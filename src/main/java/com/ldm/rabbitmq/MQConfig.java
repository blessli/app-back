package com.ldm.rabbitmq;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static final String SMS_QUEUE = "SMS";
    public static final String FEED_QUEUE="feed";
    /**
     * Direct 模式
     *
     */

    @Bean
    public Queue queue() {
        return new Queue(MQConfig.SMS_QUEUE, true);
    }
    public Queue queue1(){
        return new Queue(MQConfig.FEED_QUEUE, true);
    }
}
