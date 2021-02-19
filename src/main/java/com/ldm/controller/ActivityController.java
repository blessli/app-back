package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.entity.ActivityDetail;
import com.ldm.request.PublishActivity;
import com.ldm.service.ActivityService;
import com.ldm.service.CacheService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ldm.common.Constant;

import java.text.ParseException;

@Slf4j
@Api(tags = "活动相关接口",description = "提供活动相关的REST API")
@RestController
public class ActivityController implements InitializingBean {
    @Autowired
    ActivityService activityService;
    @Autowired
    private CacheService cacheService;

    // 直接写入mysql；redis限流
    @Action(name = "发表活动")
    @ApiOperation(value = "发表活动")
    @PostMapping(value = "/activity/add")
    public JSONResult publishActivity(@RequestBody PublishActivity request) throws ParseException {
        log.info("发表活动：{}", request.getActivityId());
        if (cacheService.limitFrequency("activity",request.getUserId())){
            log.info(frequencyHit);
            return JSONResult.fail(frequencyHit);
        }
        int ans=activityService.publishActivity(request);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    // 删除活动，同时将与活动相关的都删除
    @Action(name = "删除活动")
    @ApiOperation(value = "删除活动")
    @PostMapping("/activity/delete")
    public JSONResult deleteActivity(int activityId){
        log.info("删除活动 {}", activityId);
        int ans=activityService.deleteActivity(activityId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    // 按时间降序
    @Action(name = "获取活动列表-最新发表")
    @ApiOperation(value = "获取活动列表-最新发表")
    @GetMapping("/activities/byTime")
    public JSONResult getActivityListByTime(int userId,int pageNum,int pageSize){
        log.info("userId {} 获取最新活动列表：当前页为：{}，页大小为 {}", userId, pageNum, pageSize);
        return JSONResult.success(activityService.selectActivityListByTime(userId,pageNum,pageSize));
    }

    @Action(name = "获取活动列表-按距离排序")
    @ApiOperation(value = "获取活动列表-按距离排序")
    @GetMapping("/activities/byDistance")
    public JSONResult getActivityListByDistance(int userId,double longitude,double latitude,int pageNum,int pageSize){
        log.info("userId {} 获取最新活动列表：当前页为：{}，页大小为 {}",userId, pageNum, pageSize);
        return JSONResult.success(activityService.selectActivityByDistance(userId, longitude, latitude, pageNum, pageSize));
    }

    @Action(name = "获取活动列表-按热度排序")
    @ApiOperation(value = "获取活动列表-按热度排序")
    @GetMapping("/activities/byHot")
    public JSONResult getActivityListByHot(int userId,int pageNum,int pageSize){
        log.info("userId {} 获取最热活动列表：当前页为：{}，页大小为 {}",userId, pageNum, pageSize);
        return JSONResult.success(activityService.selectActivityByHot(userId, pageNum, pageSize));
    }

    @Action(name = "获取活动分类-按主题分类")
    @ApiOperation(value = "获取活动分类-按主题分类")
    @GetMapping("/activities/bySort")
    public JSONResult getActivityListBySort(int tagIndex,int pageNum,int pageSize){
        String activityType=Constant.ACTIVITY_TYPE[tagIndex];
        log.info("获取活动分类 {} 列表：当前页为：{}，页大小为 {}",activityType, pageNum, pageSize);
        return JSONResult.success(activityService.selectActivityBySort(activityType, pageNum, pageSize));
    }

    @Action(name = "获取我申请加入的活动")
    @ApiOperation(value = "获取我申请加入的活动")
    @GetMapping("/activities/tryJoined")
    public JSONResult getMyActivityList(int userId,int pageNum,int pageSize){
        log.info("获取用户 {} 申请加入的活动，当前页为：{}，页大小为 {}", userId, pageNum, pageSize);
        return JSONResult.success(activityService.selectMyActivityList(userId,pageNum,pageSize));
    }

    // 按时间降序
    @Action(name = "获取我发表的活动列表")
    @ApiOperation(value = "获取我发表的活动列表")
    @GetMapping("/activities/createdByMe")
    public JSONResult getActivityCreatedByMe(int userId,int pageNum,int pageSize){
        log.info("获取用户 {} 发表的活动列表: 当前页为：{}，页大小为 {}", userId, pageNum, pageSize);
        return JSONResult.success(activityService.selectActivityCreatedByMe(userId,pageNum,pageSize));
    }
    // 用户点击活动，进入详情页，如果是首次点击则浏览量+1
    @Action(name = "获取活动详情")
    @ApiOperation(value = "获取活动详情")
    @GetMapping("/activity/detail")
    public JSONResult getActivityDetail(int activityId,int userId,int pageNum,int pageSize){
        log.info("用户 {} 获取活动 {} 的详情: 当前页为：{}，页大小为 {}", userId,activityId, pageNum, pageSize);
        ActivityDetail activityDetail=activityService.selectActivityDetail(activityId, userId,pageNum,pageSize);
        if (activityDetail==null) {
            return JSONResult.deleted();
        }
        return JSONResult.success();
    }
    // 用户申请/取消申请加入活动
    @Action(name = "用户申请加入活动")
    @ApiOperation(value = "用户申请加入活动")
    @PostMapping("/activity/join")
    public JSONResult joinActivity(int activityId,int userId){
        log.info("用户 {} 申请加入活动 {}", userId,activityId);
        return JSONResult.success(activityService.joinActivity(activityId, userId));
    }


    @Action(name = "同意用户加入活动")
    @ApiOperation(value = "同意用户加入活动")
    @PostMapping("/activity/agreeJoin")
    public JSONResult agreeJoinActivity(int activityId,int userId){
        log.info("同意用户 {} 加入活动 {}", userId, activityId);
        int ans=activityService.agreeJoinActivity(activityId, userId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "拒绝用户加入活动")
    @ApiOperation(value = "拒绝用户加入活动")
    @PostMapping("/activity/disagreeJoin")
    public JSONResult disagreeJoinActivity(int activityId,int userId){
        log.info("拒绝用户 {} 加入活动 {}", userId, activityId);
        int ans=activityService.disagreeJoinActivity(activityId, userId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "获取活动成员列表")
    @ApiOperation(value = "获取活动成员列表")
    @GetMapping("/activity/member")
    public JSONResult getActivityMemberList(int activityId){
        log.info("获取活动 {} 成员列表", activityId);
        return JSONResult.success(activityService.getActivityMemberList(activityId));
    }
    private static final String frequencyHit="发表活动过于频繁，请稍后再试！！！";

    @Override
    public void afterPropertiesSet() {
        cacheService.afterPropertiesSet();
    }
}
