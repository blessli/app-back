package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.entity.ActivityIndex;
import com.ldm.request.PublishActivity;
import com.ldm.service.ActivityService;
import com.ldm.service.CacheService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@Validated
public class ActivityController {

    private String[] activityTag={"电影","美食","运行","旅游","K歌","其他"};
    @Autowired
    ActivityService activityService;
    @Autowired
    private CacheService cacheService;

    @Action(name = "获取活动列表-按时间排序")
    @GetMapping("/activities/byTime")
    public JSONResult getActivityListByTime(@Valid @Min(1) int userId, int pageNum, int pageSize){
        log.info("为用户 {} 生成最新活动列表：第 {} 页",userId, pageNum);
        return JSONResult.success(activityService.selectActivityListByTime(userId,pageNum,pageSize));
    }

    @Action(name = "获取活动列表-按距离排序")
    @GetMapping("/activities/byDistance")
    public JSONResult getActivityListByDistance(@Valid @Min(1) int userId,double longitude,double latitude,int pageNum,int pageSize){
        log.info("为用户 {} 生成最近活动列表：第 {} 页", userId,pageNum);
        return JSONResult.success(activityService.selectActivityListByDistance(userId,longitude, latitude, pageNum, pageSize));
    }

    @Action(name = "获取活动列表-按热度排序")
    @GetMapping("/activities/byHot")
    public JSONResult getActivityListByHot(@Valid @Min(1) int userId,int pageNum,int pageSize){
        log.info("为用户 {} 生成最热活动列表：第 {} 页",userId, pageNum);
        return JSONResult.success(activityService.selectActivityListByHot(userId,pageNum, pageSize));
    }

    @Action(name = "获取活动列表-按分类")
    @GetMapping("/activities/bySort")
    public JSONResult getActivityListBySort(@Valid @Max(5) @Min(0) int tagIndex, int pageNum, int pageSize){
        log.info("获取活动类型为 {} 活动列表",activityTag[tagIndex]);
        return JSONResult.success(activityService.selectActivityListBySort(activityTag[tagIndex], pageNum, pageSize));
    }

    @Action(name = "推荐活动列表")
    @GetMapping("/activities/recommend")
    public JSONResult recommendActivityList(@Valid @Min(1) int userId, int pageNum, int pageSize){
        log.info("为用户 {} 推荐活动列表：第 {} 页", userId,pageNum);
        return JSONResult.success(activityService.selectActivityListByRecommend(userId, pageNum, pageSize));
    }

    @Action(name = "获取申请加入的活动")
    @GetMapping("/activities/tryJoined")
    public JSONResult getMyActivityList(@Valid @Min(1) int userId,int pageNum,int pageSize){
        log.info("获取用户 {} 申请加入的活动", userId);
        return JSONResult.success(activityService.selectMyActivityList(userId,pageNum,pageSize));
    }

    @Action(name = "获取活动详情")
    @GetMapping("/activity/detail")
    public JSONResult getActivityDetail(@Valid @Min(1) int activityId,@Valid @Min(1) int userId,int pageNum,int pageSize) throws ParseException {
        log.info("获取活动 {} 的详情: 第 {} 页", activityId, pageNum);
        return JSONResult.success(activityService.selectActivityDetail(activityId, userId,pageNum,pageSize));
    }

    @Action(name = "获取活动成员列表")
    @GetMapping("/activity/member")
    public JSONResult getActivityMemberList(@Valid @Min(1) int activityId){
        log.info("获取活动 {} 的成员列表",activityId);
        return JSONResult.success(activityService.selectActivityMemberList(activityId));
    }

    @Action(name = "发表活动")
    @PostMapping(value = "/activity/add")
    public JSONResult publishActivity(@RequestBody @Valid PublishActivity request) throws ParseException {
        log.info("发表活动 {}", request.getActivityId());
        if (cacheService.limitFrequency("activity",request.getUserId())){
            log.info(frequencyHit);
            return JSONResult.fail(frequencyHit);
        }
        int ans=activityService.publishActivity(request);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "删除活动")
    @PostMapping("/activity/delete")
    public JSONResult deleteActivity(@Valid @RequestParam @Min(1) int activityId){
        log.info("删除活动 {}", activityId);
        int ans=activityService.deleteActivity(activityId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "获取我发表的活动列表")
    @GetMapping("/activities/createdByMe")
    public JSONResult getActivityCreatedByMe(@Valid @Min(1) int userId,int pageNum,int pageSize){
        log.info("获取用户 {} 发表的活动列表: 第 {} 页", userId, pageNum);
        return JSONResult.success(activityService.selectActivityCreatedByMe(userId,pageNum,pageSize));
    }

    @Action(name = "用户取消/申请加入活动")
    @PostMapping("/activity/join")
    public JSONResult joinActivity(@Valid @RequestParam @Min(1) int activityId,@Valid @RequestParam @Min(1) int userId){
        return JSONResult.success(activityService.joinActivity(activityId, userId));
    }

    @Action(name = "退出活动")
    @PostMapping("/activity/exit")
    public JSONResult exitActivity(@Valid @RequestParam @Min(1) int activityId,@Valid @RequestParam @Min(1) int userId){
        log.info("用户 {} 退出活动 {}",userId,activityId);
        return activityService.exitActivity(activityId, userId)>0?JSONResult.success():JSONResult.fail("error");
    }
    @Action(name = "同意加入活动")
    @PostMapping("/activity/agreeJoin")
    public JSONResult agreeJoinActivity(@Valid @RequestParam @Min(1) int activityId,@Valid @RequestParam @Min(1) int userId){
        log.info("同意用户 {} 加入活动 {}", userId, activityId);
        int ans=activityService.agreeJoinActivity(activityId, userId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "拒绝加入活动")
    @PostMapping("/activity/disagreeJoin")
    public JSONResult disagreeJoinActivity(@Valid @RequestParam @Min(1) int activityId,@Valid @RequestParam @Min(1) int userId){
        log.info("拒绝用户 {} 加入活动 {}", userId, activityId);
        int ans=activityService.disagreeJoinActivity(activityId, userId);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "分享活动")
    @PostMapping("/activity/share")
    public JSONResult shareActivity(@Valid @RequestParam @Min(1) int activityId) throws ParseException {
        log.info("分享活动 {}",activityId);
        return activityService.shareActivity(activityId)>0?JSONResult.success():JSONResult.fail("error");
    }

    private static final String frequencyHit="发表活动过于频繁，请稍后再试！！！";
}
