package com.ldm.controller;
import com.ldm.entity.activity.ActivityDetail;
import com.ldm.rabbitmq.MQSender;
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
    @Autowired
    private MQSender mqSender;
    @ApiOperation(value = "发布活动")
    @PostMapping("/activity/publish")
    public JSONResult publishActivity(@RequestBody PublishActivityRequest publishActivityRequest){
        activityService.publish(publishActivityRequest);
        return JSONResult.success();
    }
    @ApiOperation(value = "获取所有活动")
    @GetMapping("/activities")
    public JSONResult getActivityList(){
        return JSONResult.success(activityService.selectActivityList());
    }
    @ApiOperation(value = "获取活动详情")
    @GetMapping("/activity")
    public JSONResult getActivityDetail(int activityId){
        ActivityDetail activityDetail=activityService.selectActivityDetail(activityId);
        mqSender.send(activityDetail);
        return JSONResult.success(activityService.selectActivityDetail(activityId));
    }
    @ApiOperation(value = "获取评论回复详情")
    @GetMapping("/activity/reply")
    public JSONResult getActivityReplyList(int commentId){
        return JSONResult.success(activityService.selectActivityReplyList(commentId));
    }
    @ApiOperation(value = "点击进入活动详情")
    @PutMapping("/activity/click")
    public JSONResult clickActivityDetail(int activityId){
        activityService.clickActivityDetail(activityId);
        return JSONResult.success();
    }
}
