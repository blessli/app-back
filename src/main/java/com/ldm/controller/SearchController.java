package com.ldm.controller;

import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "搜索相关接口",description = "提供搜索相关的REST API")
@RestController
public class SearchController {
//    @Autowired
//    private SearchService searchService;

    @ApiOperation(value = "某个用户的历史搜索活动")
    @GetMapping("/search/history")
    public JSONResult historySearch(String userId,String type){
        System.out.println(userId);
        return JSONResult.success();
    }
    @ApiOperation(value = "热门搜索活动")
    @GetMapping("/search/hot")
    public JSONResult hotSearch(String type){
        return JSONResult.success();
    }

    @ApiOperation(value = "更新某个用户的历史搜索活动")
    @PostMapping("/search/history")
    public JSONResult updateActivityHistory(String userId,String keyword,String type){
//        searchService.updateHistorySearch(userId, keyword, type);
        return JSONResult.success();
    }

}
