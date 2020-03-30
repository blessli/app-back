package com.ldm.controller;
import com.ldm.entity.Activity;
import com.ldm.service.activity.ActivityService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ldm.request.PublishActivityRequest;

@Api(tags = "活动相关接口",description = "提供活动相关的REST API")
@RestController
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    /**
     * @title 发布活动
     * @description 直接写入mysql
     * @author lidongming
     * @updateTime 2020/3/28 23:57
     */
    @ApiOperation(value = "发表活动")
    @PostMapping("/activity/add")
    public JSONResult publishActivity(@RequestBody PublishActivityRequest request){
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
    public JSONResult editActivity(@RequestBody PublishActivityRequest publishActivityRequest){
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

    /**
     * @title 获取评论详情
     * @description 活动详情页中只实现评论列表
     * @author lidongming
     * @updateTime 2020/3/29 0:09
     */
    @ApiOperation(value = "获取评论详情")
    @GetMapping("/activity/comment")
    public JSONResult getActivityCommentList(int activityId){
        return JSONResult.success();
    }

    /**
     * @title 获取评论的回复列表
     * @description 活动详情页中点击某个评论展示回复列表
     * @author lidongming
     * @updateTime 2020/3/29 0:14
     */
    @ApiOperation(value = "获取评论的回复列表")
    @GetMapping("/activity/reply")
    public JSONResult getActivityReplyList(int commentId){
        return JSONResult.success();
    }

    /**
     * @title 发表评论
     * @description 在活动详情页发表评论
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @ApiOperation(value = "发表评论")
    @PostMapping("/activity/comment/add")
    public JSONResult publishComment(@RequestBody PublishActivityRequest publishActivityRequest){
        return JSONResult.success();
    }

    /**
     * @title 删除评论
     * @description 在活动详情页中的评论列表里删除
     * @author lidongming
     * @updateTime 2020/3/29 0:29
     */
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/activity/comment/delete")
    public JSONResult deleteComment(int commentId){
        return JSONResult.success();
    }

    /**
     * @title 发表回复
     * @description 在评论的回复列表中发表回复
     * @author lidongming
     * @updateTime 2020/3/29 0:23
     */
    @ApiOperation(value = "发表回复")
    @PostMapping("/activity/reply/add")
    public JSONResult publishReply(@RequestBody PublishActivityRequest publishActivityRequest){
        return JSONResult.success();
    }

    /**
     * @title 删除回复
     * @description 在某条评论的回复列表中将其删除
     * @author lidongming
     * @updateTime 2020/3/29 0:28
     */
    @ApiOperation(value = "删除回复")
    @DeleteMapping("/activity/reply/delete")
    public JSONResult deleteReply(int replyId){
        return JSONResult.success();
    }

    /**
     * @title 收藏活动
     * @description 用户在活动详情页收藏该活动
     * @author lidongming
     * @updateTime 2020/3/29 0:19
     */
    @ApiOperation(value = "收藏活动")
    @PostMapping("/activity/collect")
    public JSONResult collectActivity(int userId,int activityId){
        return JSONResult.success();
    }

    /**
     * @title 取消收藏活动
     * @description 用户在活动详情页取消收藏该活动
     * @author lidongming
     * @updateTime 2020/3/29 0:19
     */
    @ApiOperation(value = "取消收藏活动")
    @DeleteMapping("/activity/cancelCollect")
    public JSONResult cancelCollectActivity(int userId, int activityId){
        return JSONResult.success(new Activity());
    }
}
