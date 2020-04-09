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
//    @Scheduled(fixedRate = 1000*3600)
    public void send() {
        System.out.println("print method 2");
    }
    /**
     * @title Feed数据清理
     * @description 脚本作为定时任务启动，时间间隔由功能上线后数据增长情况决定
     * @description 脚本遍历用户的收Feed和发Feed
     * @description 判断每组有序集的数量，对大于300条的数据，从最早的记录开始剔除，直到数量小于等于300条为止
     * @author lidongming 
     * @updateTime 2020/4/9 2:01 
     */
    @Scheduled(cron = "30 10 1 * * ?")
    public void trans(){

    }
}