package com.ldm.notice;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 基于注解的定时器
 */
@Component
public class AdvanceNotice {
    /**
     * 启动时执行一次，之后每隔一个小时秒执行一次
     * 向相关用户发送短信或者推送
     * spring task默认是单线程，warning，需要使用线程池异步处理
     */
    @Scheduled(fixedRate = 1000*3600)
    public void send() {
        System.out.println("print method 2");
    }
    @Scheduled(cron = "30 10 1 * * ?")
    public void trans(){

    }
}