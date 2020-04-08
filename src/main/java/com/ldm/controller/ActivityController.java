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
    @PostMapping(value = "/activity/add",consumes = "application/json")
    public JSONResult publishActivity(@RequestBody PublishActivity request){
        log.info("发表活动");
        if (!cacheService.limitFrequency("activity",request.getUserId())){
            log.info("操作过于频繁，请稍后再试！！！");
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        int ans=activityService.publishActivity(request);
        return ans>0?JSONResult.success():JSONResult.fail("error");
    }

    /**
     * @title 删除活动
     * @description 删除活动，同时将与活动相关的都删除（方便起见，就不设置state了）
     * @author lidongming
     * @updateTime 2020/3/29 0:32
     */
    @Action(name = "删除活动")
    @DeleteMapping("/activity/delete")
    public JSONResult deleteActivity(int activityId){
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
    public JSONResult getActivityListByTime(){
        return JSONResult.success(activityService.selectActivityListByTime());
    }
    /**
     * @title 获取活动详情
     * @description 用户点击活动，进入详情页，如果是首次点击则浏览量+1
     * @author lidongming
     * @updateTime 2020/3/29 0:08
     */
    @Action(name = "获取活动详情")
    @GetMapping("/activity/detail")
    public JSONResult getActivityDetail(int activityId,int userId){
        return JSONResult.success(activityService.selectActivityDetail(activityId, userId));
    }


}
