package com.ldm.service;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lidongming
 * @ClassName FeedService.java
 * @Description 好友动态Feed流的Redis实现
 * @createTime 2020年04月09日 01:07:00
 */
public class FeedService {
    @Autowired
    private CacheService cacheService;
    /**
     * @title
     * @description 用户发帖时触发事件通知，将动态ID，用户UID和时间戳封装为一条消息.消息放入动态发布处理队列，交给队列进行异步处理。
     * @author lidongming
     * @updateTime 2020/4/9 1:20
     */
    public void publishDynamic(){

    }
}
