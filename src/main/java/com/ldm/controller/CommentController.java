package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.request.PublishComment;
import com.ldm.service.CacheService;
import com.ldm.service.CommentService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.text.ParseException;

/**
 * @author lidongming
 * @ClassName CommentController.java
 * @Description 评论服务
 * @createTime 2020年04月04日 04:37:00
 */
@Slf4j
@RestController
@Validated
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CacheService cacheService;

    @Action(name = "获取评论列表")
    @GetMapping("/comments")
    public JSONResult getCommentList(@Valid @Min(1) int itemId, @Valid @Max(1) @Min(0) int flag, int pageNum, int pageSize) {
        if (flag == 0) {
            log.info("获取活动 {} 的详情，当前页为：{}", itemId, pageNum);
        } else {
            log.info("获取动态 {} 的详情，当前页为：{}", itemId, pageNum);
        }
        return JSONResult.success(commentService.getCommentList(itemId, flag, pageNum, pageSize));
    }

    /**
     * @title 发表评论
     * @description 发表评论，flag为0则活动，flag为1则动态；redis限流
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @Action(name = "发表评论")
    @PostMapping(value = "/comment/add")
    public JSONResult publishComment(@RequestBody @Valid PublishComment request) throws ParseException {
        log.info("用户 {} 发表评论", request.getUserId());
        if (cacheService.limitFrequency("comment", request.getUserId())) {
            log.info(frequencyCommentHit);
            return JSONResult.fail(frequencyCommentHit);
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
    public JSONResult deleteComment(@Valid @RequestParam @Min(1) int itemId,@Valid @RequestParam @Max(1) @Min(0) int flag, @Valid@RequestParam @Min(1) int commentId) throws ParseException {
        if(flag == 0){
            log.info("删除活动 {} 的评论 {}", itemId, commentId);
        }else {
            log.info("删除动态 {} 的评论 {}", itemId, commentId);
        }
        return commentService.deleteComment(itemId, flag, commentId) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    private static final String frequencyCommentHit="发表评论过于频繁，请稍后再试！！！";
}
