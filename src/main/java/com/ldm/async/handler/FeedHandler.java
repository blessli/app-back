package com.ldm.async.handler;

import com.ldm.async.EventHandler;
import com.ldm.async.EventModel;
import com.ldm.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 用户动态消息采用推拉结合的方式实现
 * 如果用户粉丝量较小则直接推送，反之则等待粉丝自行拉取
 */
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    CacheService cacheService;

    @Override
    public void doHandle(EventModel model) {

    }
}
