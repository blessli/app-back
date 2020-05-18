package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.request.PublishReply;
import com.ldm.service.CacheService;
import com.ldm.service.ReplyService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * @author lidongming
 * @ClassName ReplyController.java
 * @Description 回复服务
 * @createTime 2020年04月17日 19:16:00
 */
@Slf4j
@RestController
@Validated
public class ReplyController {
    
    @Autowired
    private ReplyService replyService;

    @Autowired
    private CacheService cacheService;

    @Action(name = "获取回复列表")
    @GetMapping("/replies")
    public JSONResult getReplyList(@Valid @Min(1) int commentId, int pageNum, int pageSize) {
        log.info("获取评论 {} 的回复列表，当前页为：{}", commentId, pageNum);
        return JSONResult.success(replyService.getReplyList(commentId, pageNum, pageSize));
    }

    @Action(name = "发表回复")
    @PostMapping(value = "/reply/add")
    public JSONResult publishReply(@RequestBody @Valid PublishReply request){
        log.info("用户 {} 回复用户{}", request.getUserId(), request.getToUserId());
        if (cacheService.limitFrequency("reply",request.getUserId())){
            log.info(frequencyReplyHit);
            return JSONResult.fail(frequencyReplyHit);
        }
        return replyService.publishReply(request) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    @Action(name = "删除回复")
    @PostMapping("/reply/delete")
    public JSONResult deleteReply(@Valid @RequestParam @Min(1) int commentId,@Valid @RequestParam @Min(1) int replyId) {
        log.info("删除评论 {} 的回复 {}", commentId, replyId);
        return replyService.deleteReply(commentId, replyId) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }
    private static final String frequencyReplyHit="发表回复过于频繁，请稍后再试！！！";

}
