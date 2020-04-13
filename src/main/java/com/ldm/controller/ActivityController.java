package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.request.PublishActivity;
import com.ldm.service.ActivityService;
import com.ldm.service.CacheService;
import com.ldm.util.JSONResult;
import com.ldm.service.SensitiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ActivityController {
    @Autowired
    ActivityService activityService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SensitiveService sensitiveService;

    /**
     * @title 发布活动
     * @description 直接写入mysql；redis限流
     * @author lidongming
     * @updateTime 2020/3/28 23:57
     */
    @Action(name = "发表活动")
    @PostMapping(value = "/activity/add")
    public JSONResult publishActivity(@RequestBody PublishActivity request) {

        log.info("发表活动：{}", request.getActivityId());
        if (cacheService.limitFrequency("activity", request.getUserId())) {
            log.info("操作过于频繁，请稍后再试！！！");
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        int ans = activityService.publishActivity(request);

        //可以考虑将活动数据同时缓存到redis
        //添加数据先向数据库添加，再向redis添加

        return ans > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    /**
     * @title 删除活动
     * @description 删除活动，同时将与活动相关的都删除（方便起见，就不设置state了）
     * @author lidongming
     * @updateTime 2020/3/29 0:32
     */
    @Action(name = "删除活动")
    @PostMapping("/activity/delete")
    public JSONResult deleteActivity(int activityId) {
        int ans = activityService.deleteActivity(activityId);

        //有es的话，是否要将es里相应的数据也删除
        log.info("删除活动：{}", activityId);
        return ans > 0 ? JSONResult.success() : JSONResult.fail("error");
    }

    /**
     * @title 获取最新发表的活动列表
     * @description 按时间降序
     * @author lidongming
     * @updateTime 2020/3/28 23:58
     */
    @Action(name = "获取活动列表-最新发表")
    @GetMapping("/activities/byTime")
    public JSONResult getActivityListByTime(int pageNum, int pageSize) {

        log.info("获取最新活动列表：第 {} 页", pageNum);
        return JSONResult.success(activityService.selectActivityListByTime(pageNum, pageSize));
    }

    @Action(name = "获取我申请加入的活动")
    @GetMapping("/activities/tryJoined")
    public JSONResult getMyActivityList(int userId, int pageNum, int pageSize) {
        log.info("获取用户 {} 申请加入的活动", userId);
        return JSONResult.success(activityService.selectMyActivityList(userId, pageNum, pageSize));
    }

    /**
     * @title 获取我发表的活动列表
     * @description 按时间降序
     * @author lidongming
     * @updateTime 2020/3/28 23:58
     */
    @Action(name = "获取我发表的活动列表")
    @GetMapping("/activities/createdByMe")
    public JSONResult getActivityCreatedByMe(int userId, int pageNum, int pageSize) {

        log.info("获取用户 {} 发表的活动列表: 第 {} 页", userId, pageNum);
        return JSONResult.success(activityService.selectActivityCreatedByMe(userId, pageNum, pageSize));
    }

    /**
     * @title 获取活动详情
     * @description 用户点击活动，进入详情页，如果是首次点击则浏览量+1
     * @author lidongming
     * @updateTime 2020/3/29 0:08
     */
    @Action(name = "获取活动详情")
    @GetMapping("/activity/detail")
    public JSONResult getActivityDetail(int activityId, int userId, int pageNum, int pageSize) {

        log.info("获取活动 {} 的详情: 第 {} 页", activityId, pageNum);
        return JSONResult.success(activityService.selectActivityDetail(activityId, userId, pageNum, pageSize));
    }

    /**
     * @title 用户申请加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 18:06
     */
    @Action(name = "用户申请加入活动")
    @PostMapping("/activity/tryJoin")
    public JSONResult tryJoinActivity(int activityId, int userId) {
        log.info("用户 {} 申请加入活动 {}", userId, activityId);
        return JSONResult.success(activityService.tryJoinActivity(activityId, userId));
    }

    /**
     * @title 用户取消申请加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 18:06
     */
    @Action(name = "用户取消申请加入活动")
    @PostMapping("/activity/cancelJoin")
    public JSONResult cancelJoinActivity(int activityId, int userId) {
        log.info("用户 {} 取消加入活动 {}", userId, activityId);
        return JSONResult.success(activityService.cancelJoinActivity(activityId, userId));
    }

    @Action(name = "获取申请通知")
    @GetMapping("/activity/notice")
    public JSONResult getActivityApplyList(int userId, int pageNum, int pageSize) {
        log.info("正在获取用户 {} 的申请通知，当前页为 {}", userId, pageNum);
        return JSONResult.success(activityService.selectActivityApplyList(userId, pageNum, pageSize));
    }

    @Action(name = "同意用户加入活动")
    @PostMapping("/activity/agreeJoin")
    public JSONResult agreeJoinActivity(int activityId, int userId) {
        log.info("用户 {} 同意加入活动 {}", userId, activityId);
        return JSONResult.success(activityService.cancelJoinActivity(activityId, userId));
    }

    @Action(name = "不同意用户加入活动")
    @PostMapping("/activity/disagreeJoin")
    public JSONResult disagreeJoinActivity(int activityId, int userId) {
        log.info("用户 {} 拒绝加入活动 {}", userId, activityId);
        return JSONResult.success(activityService.cancelJoinActivity(activityId, userId));
    }
}
