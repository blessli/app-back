package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.service.NoticeService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lidongming
 * @ClassName NoticeController.java
 * @Description 通知服务
 * @createTime 2020年04月15日 13:20:00
 */
@Slf4j
@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;
    @Action(name = "获取申请通知")
    @GetMapping("/notice/apply")
    public JSONResult getApplyNotice(int userId, int pageNum, int pageSize){
        log.debug("正在获取用户 {} 的申请通知，当前页为 {}", userId, pageNum);
        return JSONResult.success(noticeService.selectApplyNotice(userId,pageNum,pageSize));
    }

    @Action(name = "获取点赞通知")
    @GetMapping("/notice/like")
    public JSONResult getLikeNotice(int userId,int pageNum,int pageSize){
        log.debug("获取用户 {} 的点赞通知，当前页为：{}", userId, pageNum);
        return JSONResult.success(noticeService.selectLikeNotice(userId,pageNum,pageSize));
    }

    /**
     * @title 获取回复通知
     * @description 使用redis实现
     * @author lidongming
     * @updateTime 2020/4/11 14:51
     */
    @Action(name = "获取评论通知")
    @GetMapping("/notice/reply")
    public JSONResult getCommentNotice(int userId, int pageNum, int pageSize){
        log.debug("获取用户 {} 的评论通知，当前页为：{}", userId, pageNum);
        return JSONResult.success(noticeService.selectReplyNotice(userId, pageNum, pageSize));
    }

    @Action(name = "获取关注通知")
    @GetMapping("/notice/follow")
    public JSONResult getFollowNotice(int userId, int pageNum, int pageSize){
        log.debug("获取用户 {} 的关注通知，当前页为：{}", userId, pageNum);
        return JSONResult.success(noticeService.selectFollowNotice(userId, pageNum, pageSize));
    }
}
