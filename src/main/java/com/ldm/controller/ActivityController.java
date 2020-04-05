package com.ldm.controller;
import com.ldm.entity.Activity;
import com.ldm.request.PublishActivity;
import com.ldm.service.ActivityService;
import com.ldm.service.CacheService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "活动相关接口",description = "提供活动相关的REST API")
@RestController
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private CacheService cacheService;

    /**
     * @title 发布活动
     * @description 直接写入mysql；redis限流
     * @author lidongming
     * @updateTime 2020/3/28 23:57
     */
    @ApiOperation(value = "发表活动")
    @PostMapping("/activity/add")
    public JSONResult publishActivity(@RequestBody PublishActivity request){
        if (!cacheService.limitFrequency(request.getUserId())){
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return JSONResult.success();
    }

    /**
     * @title 获取发表的活动列表
     * @description 时间降序
     * @author lidongming
     * @updateTime 2020/3/29 0:38
     */
    @ApiOperation(value = "获取发表的活动列表")
    @GetMapping("/activities/added")
    public JSONResult getAddedActivityList(){
        return JSONResult.success();
    }

    /**
     * @title 编辑活动
     * @description 对活动内容进行编辑
     * @author lidongming
     * @updateTime 2020/3/29 0:31
     */
    @ApiOperation(value = "编辑活动")
    @PutMapping("/activity/edit")
    public JSONResult editActivity(@RequestBody PublishActivity publishActivityRequest){
        return JSONResult.success();
    }

    /**
     * @title 删除活动
     * @description 删除活动，同时将与活动相关的都删除（方便起见，就不设置state了）
     * @author lidongming
     * @updateTime 2020/3/29 0:32
     */
    @ApiOperation(value = "删除活动")
    @DeleteMapping("/activity/delete")
    public JSONResult deleteActivity(int activityId){
        return JSONResult.success();
    }

    /**
     * @title 获取最新发表的活动列表
     * @description 按时间降序
     * @author lidongming
     * @updateTime 2020/3/28 23:58
     */
    @ApiOperation(value = "获取活动列表-最新发表")
    @GetMapping("/activities/byTime")
    public JSONResult getActivityListByTime(){
        return JSONResult.success();
    }

    /**
     * @title 获取最热活动
     * @description 按热度降序，采用Hacker News算法
     * @author lidongming
     * @updateTime 2020/3/29 0:03
     */
    @ApiOperation(value = "获取活动列表-最热")
    @GetMapping("/activities/byHot")
    public JSONResult getActivityListByHot(){
        return JSONResult.success();
    }

    /**
     * @title 获取最新回复的活动列表
     * @description 按发布者最新回复的时间降序
     * @author lidongming
     * @updateTime 2020/3/29 0:11
     */
    @ApiOperation(value = "获取活动列表-最新回复")
    @GetMapping("/activities/byReplyTime")
    public JSONResult getActivityListByReplyTime(){
        return JSONResult.success();
    }
    /**
     * @title 获取活动详情
     * @description 用户点击活动，进入详情页，如果是首次点击则浏览量+1
     * @author lidongming
     * @updateTime 2020/3/29 0:08
     */
    @ApiOperation(value = "获取活动详情")
    @GetMapping("/activity/detail")
    public JSONResult getActivityDetail(int activityId){
        return JSONResult.success();
    }


}
