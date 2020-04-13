package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.entity.DynamicDetail;
import com.ldm.request.PublishDynamic;
import com.ldm.service.CacheService;
import com.ldm.service.CommentService;
import com.ldm.service.DynamicService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class DynamicController {
    @Autowired
    private DynamicService dynamicService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private CommentService commentService;


    @Action(name = "发布动态")
    @PostMapping(value = "/dynamic/add", consumes = "application/json")
    public JSONResult publishDynamic(@RequestBody PublishDynamic request) {

        log.info("用户 {} 发表动态", request.getUserId());
        if (cacheService.limitFrequency("dynamic", request.getUserId())) {
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        //操作过于频繁后在redis是否应该设置一个过期时间，使用户在相应时间后能继续发布
        return dynamicService.publish(request) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    @Action(name = "删除动态")
    @PostMapping("/dynamic/delete")
    public JSONResult deleteDynamic(int dynamicId) {
        log.info("删除动态 {}", dynamicId);
        //有es的话是否也该删除相应数据
        return dynamicService.deleteDynamic(dynamicId) > 0 ? JSONResult.success() : JSONResult.fail("error");
    }


    /**
     * @title 获取已关注者发表的动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/7 1:33
     */
    @Action(name = "获取所有动态")
    @GetMapping("/dynamics/byTime")
    public JSONResult getDynamicList(int userId, int pageNum, int pageSize) {

        log.info("获取用户 {} 的动态，当前页为：{}", userId, pageNum);

        return JSONResult.success(dynamicService.selectDynamicList(userId, pageNum, pageSize));
    }

    @Action(name = "获取我的动态")
    @GetMapping("/dynamics/my")
    public JSONResult getMyDynamicList(int userId, int pageNum, int pageSize) {
        log.info("获取用户 {} 的动态，当前页为：{}", userId, pageNum);
        return JSONResult.success(dynamicService.selectMyDynamicList(userId, pageNum, pageSize));
    }


    @Action(name = "点赞动态")
    @PostMapping("/dynamic/like")
    public JSONResult likeDynamic(int dynamicId, int userId) {

        log.info("用户 {} 给动态 {} 点赞", userId, dynamicId);
        dynamicService.likeDynamic(dynamicId, userId);
        return JSONResult.success();
    }


    @Action(name = "取消点赞动态")
    @PostMapping("/dynamic/cancelLike")
    public JSONResult cancelLikeDynamic(int dynamicId, int userId) {

        log.info("用户 {} 取消给动态 {} 点赞", userId, dynamicId);
        dynamicService.cancelLikeDynamic(dynamicId, userId);
        return JSONResult.success();
    }

    @Action(name = "获取动态详情")
    @GetMapping("/dynamic/detail")
    public JSONResult getDynamicDetail(int dynamicId, int userId, int pageNum, int pageSize) {

        log.info("获取动态 {} 的详情，当前页为： {}", dynamicId, pageNum);
        DynamicDetail dynamicDetail = dynamicService.selectDynamicDetail(dynamicId, userId);
        dynamicDetail.setCommentList(commentService.getCommentList(dynamicId, 1, pageNum, pageSize));
        return JSONResult.success(dynamicDetail);
    }

    @Action(name = "获取动态点赞通知")
    @GetMapping("/dynamic/likeNotice")
    public JSONResult selectLikeNotice(int userId, int pageNum, int pageSize) {

        log.info("获取用户 {} 的点赞通知，当前页为：{}", userId, pageNum);
        return JSONResult.success(dynamicService.selectLikeNotice(userId, pageNum, pageSize));
    }

}
