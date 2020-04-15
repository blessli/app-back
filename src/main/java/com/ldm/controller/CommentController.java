package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import com.ldm.service.CacheService;
import com.ldm.service.CommentService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lidongming
 * @ClassName CommentController.java
 * @Description 评论服务
 * @createTime 2020年04月04日 04:37:00
 */
@Slf4j
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CacheService cacheService;

    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming
     * @updateTime 2020/3/29 0:09
     */
    @Action(name = "获取评论列表")
    @ApiOperation(value = "获取评论列表")
    @GetMapping("/comments")
    public JSONResult getCommentList(int itemId, int flag, int pageNum, int pageSize) {
        if (flag == 0) {
            log.debug("获取活动 {} 的详情，当前页为：{}", itemId, pageNum);
        } else {
            log.debug("获取动态 {} 的详情，当前页为：{}", itemId, pageNum);
        }
        return JSONResult.success(commentService.getCommentList(itemId, flag, pageNum, pageSize));
    }

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
        return JSONResult.success(commentService.getReplyList(commentId, pageNum, pageSize));
    }

    /**
     * @title 发表评论
     * @description 发表评论，flag为0则活动，flag为1则动态；redis限流
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @Action(name = "发表评论")
    @PostMapping(value = "/comment/add")
    public JSONResult publishComment(@RequestBody PublishComment request) {
        log.debug("用户 {} 给用户 {} 发表评论", request.getUserId(), request.getToUserId());
        if (cacheService.limitFrequency("comment", request.getUserId())) {
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return commentService.publishComment(request) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    /**
     * @title 删除评论
     * @description 直接删除
     * @author lidongming
     * @updateTime 2020/3/29 0:29
     */
    @Action(name = "删除评论")
    @PostMapping("/comment/delete")
    public JSONResult deleteComment(int itemId, int flag, int commentId) {
        if(flag == 0){
            log.debug("删除活动 {} 的评论 {}", itemId, commentId);
        }else {
            log.debug("删除动态 {} 的评论 {}", itemId, commentId);
        }
        return commentService.deleteComment(itemId, flag, commentId) > 0 ? JSONResult.success() : JSONResult.fail("error");
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
        log.debug("用户 {} 给 {} 回复评论", request.getFromUserId(), request.getToUserId());
        if (cacheService.limitFrequency("reply",request.getFromUserId())){
            log.debug(frequencyReplyHit);
            return JSONResult.fail(frequencyReplyHit);
        }
        return commentService.publishReply(request) > 0 ? JSONResult.success() : JSONResult.fail("error");
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
        return commentService.deleteReply(commentId, replyId) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    private static final String frequencyCommentHit="发表评论过于频繁，请稍后再试！！！";
    private static final String frequencyReplyHit="发表回复过于频繁，请稍后再试！！！";
}
