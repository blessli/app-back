package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.entity.DynamicDetail;
import com.ldm.request.PublishDynamic;
import com.ldm.service.CacheService;
import com.ldm.service.CommentService;
import com.ldm.service.DynamicService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "动态相关接口",description = "提供动态相关的REST API")
@RestController
public class DynamicController {
    @Autowired
    private DynamicService dynamicService;
    @Autowired
    private CacheService cacheService;

    @Autowired
    private CommentService commentService;

    @Action(name = "发布动态")
    @ApiOperation(value = "发布动态")
    @PostMapping(value = "/dynamic/add")
    public JSONResult publishDynamic(@RequestBody PublishDynamic request){
        log.info("用户 {} 发表动态内容 {}", request.getUserId(),request);
        if (cacheService.limitFrequency("dynamic",request.getUserId())){
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return dynamicService.publish(request)>0?JSONResult.success():JSONResult.fail("error");
    }
    @Action(name = "删除动态")
    @ApiOperation(value = "删除动态")
    @PostMapping("/dynamic/delete")
    public JSONResult deleteDynamic(int dynamicId){
        log.info("删除动态 {}", dynamicId);
        return dynamicService.deleteDynamic(dynamicId)>0?JSONResult.success():JSONResult.fail("error");
    }

    @Action(name = "获取所有动态")
    @ApiOperation(value = "获取所有动态")
    @GetMapping("/dynamics/byTime")
    public JSONResult getDynamicList(int userId,int pageNum,int pageSize){
        log.info("用户 {} 获取最新动态列表，当前页为：{}，页大小为 {}", userId, pageNum, pageSize);
        return JSONResult.success(dynamicService.selectDynamicList(userId,pageNum,pageSize));
    }

    @Action(name = "获取我的动态")
    @ApiOperation(value = "获取我的动态")
    @GetMapping("/dynamics/my")
    public JSONResult getMyDynamicList(int userId,int pageNum,int pageSize){
        log.info("获取用户 {} 的动态，当前页为：{}，页大小为 {}", userId, pageNum, pageSize);
        return JSONResult.success(dynamicService.selectMyDynamicList(userId,pageNum,pageSize));
    }
    @Action(name = "取消/点赞动态")
    @ApiOperation(value = "取消/点赞动态")
    @PostMapping("/dynamic/like")
    public JSONResult likeDynamic(int dynamicId,int userId){
        dynamicService.likeDynamic(dynamicId,userId);
        return JSONResult.success();
    }
    @Action(name = "获取动态详情")
    @ApiOperation(value = "获取动态详情")
    @GetMapping("/dynamic/detail")
    public JSONResult getDynamicDetail(int dynamicId,int userId,int pageNum,int pageSize){
        log.info("用户 {} 获取动态 {} 的详情，当前页为： {}", userId,dynamicId, pageNum);
        DynamicDetail dynamicDetail=dynamicService.selectDynamicDetail(dynamicId, userId);
        if (dynamicDetail==null) {
            return JSONResult.deleted();
        }
        dynamicDetail.setCommentList(commentService.getCommentList(dynamicId,1,pageNum,pageSize));
        return JSONResult.success(dynamicDetail);
    }

}
