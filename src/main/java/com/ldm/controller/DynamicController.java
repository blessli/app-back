package com.ldm.controller;
import com.ldm.request.PublishDynamic;
import com.ldm.service.CacheService;
import com.ldm.service.DynamicService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "动态相关接口",description = "提供动态相关的REST API")
@RestController
public class DynamicController {
    @Autowired
    private DynamicService dynamicService;
    @Autowired
    private CacheService cacheService;
    @ApiOperation(value = "发布动态")
    @PostMapping("/dynamic/publish")
    public JSONResult publishDynamic(@RequestBody PublishDynamic request){
        if (!cacheService.limitFrequency(request.getUserId())){
            return JSONResult.fail("发表评论过于频繁，请稍后再试！！！");
        }
        return JSONResult.success();
    }
    @ApiOperation(value = "删除动态")
    @DeleteMapping("/dynamic")
    public JSONResult deleteDynamic(int dynamicId){
        return null;
    }
    /**
     * @title 获取已关注者发表的动态
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 5:45
     */
    @ApiOperation(value = "获取所有动态")
    @GetMapping("/dynamics")
    public JSONResult getDynamicList(int userId){
        return JSONResult.success(dynamicService.selectDynamicList(userId));
    }
    @ApiOperation(value = "点赞动态")
    @PostMapping("/dynamic/like")
    public JSONResult likeDynamic(int dynamicId,String userId){
        return null;
    }
    @ApiOperation(value = "取消点赞动态")
    @DeleteMapping("/dynamic/like")
    public JSONResult cancelLikeDynamic(int dynamicId,String userId){
        return null;
    }
}
