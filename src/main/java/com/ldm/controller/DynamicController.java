package com.ldm.controller;
import com.ldm.request.PublishDynamic;
import com.ldm.service.dynamic.DynamicService;
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
    @ApiOperation(value = "发布动态")
    @PostMapping("/dynamic/publish")
    public JSONResult publishDynamic(@RequestBody PublishDynamic request){
        return JSONResult.success();
    }
    @ApiOperation(value = "删除动态")
    @DeleteMapping("/dynamic")
    public JSONResult deleteDynamic(int dynamicId){
        return null;
    }
    @ApiOperation(value = "获取所有动态")
    @GetMapping("/dynamics")
    public JSONResult getDynamicList(String userId){
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
