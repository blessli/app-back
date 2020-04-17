package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.request.PublishActivity;
import com.ldm.service.ActivityService;
import com.ldm.service.CacheService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
public class ActivityController implements InitializingBean {
    @Autowired
    ActivityService activityService;
    @Autowired
    private CacheService cacheService;

    /**
     * @title 发布活动
     * @description 直接写入mysql；redis限流
     * @author lidongming
     * @updateTime 2020/3/28 23:57
     */
    @Action(name = "发表活动")
    @PostMapping(value = "/activity/add")
    public JSONResult publishActivity(@RequestBody PublishActivity request) throws ParseException {
        log.debug("发表活动：{}", request.getActivityId());
        if (cacheService.limitFrequency("activity",request.getUserId())){
            log.debug(frequencyHit);
            return JSONResult.fail(frequencyHit);
        }
        int ans=activityService.publishActivity(request);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    /**
     * @title 删除活动
     * @description 删除活动，同时将与活动相关的都删除
     * @author lidongming
     * @updateTime 2020/3/29 0:32
     */
    @Action(name = "删除活动")
    @PostMapping("/activity/delete")
    public JSONResult deleteActivity(int activityId){
        log.debug("删除活动：{}", activityId);
        int ans=activityService.deleteActivity(activityId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    /**
     * @title 获取最新发表的活动列表
     * @description 按时间降序
     * @author lidongming
     * @updateTime 2020/3/28 23:58
     */
    @Action(name = "获取活动列表-最新发表")
    @GetMapping("/activities/byTime")
    public JSONResult getActivityListByTime(int userId,int pageNum,int pageSize){
        log.debug("获取最新活动列表：第 {} 页", pageNum);
        return JSONResult.success(activityService.selectActivityListByTime(userId,pageNum,pageSize));
    }

    @Action(name = "获取活动列表-按距离排序")
    @GetMapping("/activities/byDistance")
    public JSONResult getActivityListByDistance(int userId,double longitude,double latitude,int pageNum,int pageSize){
        log.debug("获取最新活动列表：第 {} 页", pageNum);
        return JSONResult.success(activityService.selectActivityByDistance(userId, longitude, latitude, pageNum, pageSize));
    }

    @Action(name = "获取我申请加入的活动")
    @GetMapping("/activities/tryJoined")
    public JSONResult getMyActivityList(int userId,int pageNum,int pageSize){
        log.debug("获取用户 {} 申请加入的活动", userId);
        return JSONResult.success(activityService.selectMyActivityList(userId,pageNum,pageSize));
    }

    /**
     * @title 获取我发表的活动列表
     * @description 按时间降序
     * @author lidongming
     * @updateTime 2020/3/28 23:58
     */
    @Action(name = "获取我发表的活动列表")
    @GetMapping("/activities/createdByMe")
    public JSONResult getActivityCreatedByMe(int userId,int pageNum,int pageSize){
        log.debug("获取用户 {} 发表的活动列表: 第 {} 页", userId, pageNum);
        return JSONResult.success(activityService.selectActivityCreatedByMe(userId,pageNum,pageSize));
    }
    /**
     * @title 获取活动详情
     * @description 用户点击活动，进入详情页，如果是首次点击则浏览量+1
     * @author lidongming
     * @updateTime 2020/3/29 0:08
     */
    @Action(name = "获取活动详情")
    @GetMapping("/activity/detail")
    public JSONResult getActivityDetail(int activityId,int userId,int pageNum,int pageSize){
        log.debug("获取活动 {} 的详情: 第 {} 页", activityId, pageNum);
        return JSONResult.success(activityService.selectActivityDetail(activityId, userId,pageNum,pageSize));
    }
    /**
     * @title 用户申请/取消申请加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 18:06
     */
    @Action(name = "用户申请加入活动")
    @PostMapping("/activity/join")
    public JSONResult joinActivity(int activityId,int userId){
        return JSONResult.success(activityService.joinActivity(activityId, userId));
    }


    @Action(name = "同意用户加入活动")
    @PostMapping("/activity/agreeJoin")
    public JSONResult agreeJoinActivity(int activityId,int userId){
        log.debug("同意用户 {} 加入活动 {}", userId, activityId);
        int ans=activityService.agreeJoinActivity(activityId, userId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "不同意用户加入活动")
    @PostMapping("/activity/disagreeJoin")
    public JSONResult disagreeJoinActivity(int activityId,int userId){
        log.debug("拒绝用户 {} 加入活动 {}", userId, activityId);
        int ans=activityService.disagreeJoinActivity(activityId, userId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }
    private static final String frequencyHit="发表活动过于频繁，请稍后再试！！！";

    @Override
    public void afterPropertiesSet() {
        cacheService.afterPropertiesSet();
    }
}
