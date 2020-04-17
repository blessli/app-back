package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.request.PublishReply;
import com.ldm.service.CacheService;
import com.ldm.service.ReplyService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lidongming
 * @ClassName ReplyController.java
 * @Description 回复服务
 * @createTime 2020年04月17日 19:16:00
 */
@Slf4j
@RestController
public class ReplyController {
    
    @Autowired
    private ReplyService replyService;

    @Autowired
    private CacheService cacheService;
    /**
     * @title 获取回复列表
     * @description 活动详情页中点击某个评论展示回复列表
     * @author lidongming
     * @updateTime 2020/3/29 0:14
     */
    @Action(name = "获取回复列表")
    @GetMapping("/replies")
    public JSONResult getReplyList(int commentId, int pageNum, int pageSize) {
        log.debug("获取评论 {} 的回复列表，当前页为：{}", commentId, pageNum);
        return JSONResult.success(replyService.getReplyList(commentId, pageNum, pageSize));
    }


    /**
     * @title 发表回复
     * @description 在评论的回复列表中发表回复redis限流
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @Action(name = "发表回复")
    @PostMapping(value = "/reply/add")
    public JSONResult publishReply(@RequestBody PublishReply request){
        log.debug("用户 {} 给 {} 回复评论", request.getUserId(), request.getToUserId());
        if (cacheService.limitFrequency("reply",request.getUserId())){
            log.debug(frequencyReplyHit);
            return JSONResult.fail(frequencyReplyHit);
        }
        return replyService.publishReply(request) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    /**
     * @title 删除回复
     * @description 直接删除
     * @author lidongming
     * @updateTime 2020/3/29 0:28
     */
    @Action(name = "删除回复")
    @PostMapping("/reply/delete")
    public JSONResult deleteReply(int commentId, int replyId) {
        log.debug("删除评论 {} 的回复 {}", commentId, replyId);
        return replyService.deleteReply(commentId, replyId) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }
    private static final String frequencyReplyHit="发表回复过于频繁，请稍后再试！！！";

}
