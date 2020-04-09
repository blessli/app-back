package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import com.ldm.service.CacheService;
import com.ldm.service.CommentService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lidongming
 * @ClassName CommentController.java
 * @Description TODO
 * @createTime 2020年04月04日 04:37:00
 */
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
    public JSONResult getActivityCommentList(int itemId,int flag){
        return JSONResult.success(commentService.getActivityCommentList(itemId, flag));
    }

    /**
     * @title 获取回复列表
     * @description 活动详情页中点击某个评论展示回复列表
     * @author lidongming
     * @updateTime 2020/3/29 0:14
     */
    @Action(name = "获取回复列表")
    @GetMapping("/replies")
    public JSONResult getActivityReplyList(int commentId){
        return JSONResult.success(commentService.getActivityReplyList(commentId));
    }

    /**
     * @title 发表评论
     * @description 发表评论，flag为0则活动，flag为1则动态；redis限流
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @Action(name = "发表评论")
    @RequestMapping(value = "/comment/add",method = RequestMethod.POST,consumes = "application/json;charset=utf-8")
    public JSONResult publishComment(@RequestBody PublishComment request){
        if (cacheService.limitFrequency("comment",request.getUserId())){
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return commentService.publishComment(request)>0?JSONResult.success():JSONResult.fail("error");
    }

    /**
     * @title 删除评论
     * @description 直接删除
     * @author lidongming
     * @updateTime 2020/3/29 0:29
     */
    @Action(name = "删除评论")
    @DeleteMapping("/comment/delete")
    public JSONResult deleteComment(int commentId){
        return commentService.deleteComment(commentId)>0?JSONResult.success():JSONResult.fail("error");
    }

    /**
     * @title 发表回复
     * @description 在评论的回复列表中发表回复redis限流
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @Action(name = "发表回复")
    @PostMapping(value = "/reply/add",consumes = "application/json")
    public JSONResult publishReply(@RequestBody PublishReply request){

        if (cacheService.limitFrequency("reply",request.getFromUserId())){
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return commentService.publishReply(request)>0?JSONResult.success():JSONResult.fail("error");
    }

    /**
     * @title 删除回复
     * @description 直接删除
     * @author lidongming
     * @updateTime 2020/3/29 0:28
     */
    @Action(name = "删除回复")
    @DeleteMapping("/reply/delete")
    public JSONResult deleteReply(int replyId){
        return commentService.deleteReply(replyId)>0?JSONResult.success():JSONResult.fail("error");
    }
}
