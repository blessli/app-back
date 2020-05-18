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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.text.ParseException;

@Slf4j
@RestController
@Validated
public class DynamicController {
    @Autowired
    private DynamicService dynamicService;
    @Autowired
    private CacheService cacheService;

    @Autowired
    private CommentService commentService;

    @Action(name = "获取好友动态")
    @GetMapping("/dynamics/byTime")
    public JSONResult getDynamicList(@Valid @Min(1) int userId, int pageNum, int pageSize){
        log.info("获取用户 {} 的好友动态，当前页为：{}", userId, pageNum);
        return JSONResult.success(dynamicService.selectDynamicList(userId,pageNum,pageSize));
    }

    @Action(name = "获取我的动态")
    @GetMapping("/dynamics/my")
    public JSONResult getMyDynamicList(@Valid @Min(1) int userId,int pageNum,int pageSize){
        log.info("获取用户 {} 的动态列表，当前页为：{}", userId, pageNum);
        return JSONResult.success(dynamicService.selectMyDynamicList(userId,pageNum,pageSize));
    }

    @Action(name = "获取动态详情")
    @GetMapping("/dynamic/detail")
    public JSONResult getDynamicDetail(@Valid @Min(1) int dynamicId,@Valid @Min(1) int userId,int pageNum,int pageSize){
        log.info("获取动态 {} 的详情，当前页为： {}", dynamicId, pageNum);
        DynamicDetail dynamicDetail=dynamicService.selectDynamicDetail(dynamicId, userId);
        dynamicDetail.setCommentList(commentService.getCommentList(dynamicId,1,pageNum,pageSize));
        return JSONResult.success(dynamicDetail);
    }

    @Action(name = "发布动态")
    @PostMapping(value = "/dynamic/add")
    public JSONResult publishDynamic(@RequestBody @Valid PublishDynamic request) throws ParseException {
        log.info("用户 {} 发表动态", request.getUserId());
        if (cacheService.limitFrequency("dynamic",request.getUserId())){
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return dynamicService.publish(request)>0?JSONResult.success():JSONResult.fail("error");
    }
    @Action(name = "删除动态")
    @PostMapping("/dynamic/delete")
    public JSONResult deleteDynamic(@Valid @RequestParam @Min(1) int dynamicId){
        log.info("删除动态 {}", dynamicId);
        return dynamicService.deleteDynamic(dynamicId)>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "取消/点赞动态")
    @PostMapping("/dynamic/like")
    public JSONResult likeDynamic(@Valid @RequestParam @Min(1) int dynamicId,@Valid @RequestParam @Min(1) int userId) throws ParseException {
        dynamicService.likeDynamic(dynamicId,userId);
        return JSONResult.success();
    }

}
