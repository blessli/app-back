package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.request.PublishDynamic;
import com.ldm.service.CacheService;
import com.ldm.service.DynamicService;
import com.ldm.util.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DynamicController {
    @Autowired
    private DynamicService dynamicService;
    @Autowired
    private CacheService cacheService;
    @Action(name = "发布动态")
    @PostMapping(value = "/dynamic/add",consumes = "application/json")
    public JSONResult publishDynamic(@RequestBody PublishDynamic request){
        if (cacheService.limitFrequency("dynamic",request.getUserId())){
            return JSONResult.fail("操作过于频繁，请稍后再试！！！");
        }
        return dynamicService.publish(request)>0?JSONResult.success():JSONResult.fail("error");
    }
    @Action(name = "删除动态")
    @DeleteMapping("/dynamic")
    public JSONResult deleteDynamic(int dynamicId){
        return dynamicService.deleteDynamic(dynamicId)>0?JSONResult.success():JSONResult.fail("error");
    }
    /**
     * @title 获取已关注者发表的动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/7 1:33
     */
    @Action(name = "获取所有动态")
    @GetMapping("/dynamics")
    public JSONResult getDynamicList(int userId){
        return JSONResult.success(dynamicService.selectDynamicList(userId));
    }
    @Action(name = "点赞动态")
    @PostMapping("/dynamic/like")
    public JSONResult likeDynamic(int dynamicId,String userId){
        return null;
    }
    @Action(name = "取消点赞动态")
    @DeleteMapping("/dynamic/like")
    public JSONResult cancelLikeDynamic(int dynamicId,String userId){
        return null;
    }
}
