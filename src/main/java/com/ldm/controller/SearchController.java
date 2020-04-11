package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.entity.Activity;
import com.ldm.service.ActivityService;
import com.ldm.service.SearchService;
import com.ldm.util.JSONResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController implements InitializingBean {
    @Autowired
    private SearchService searchService;

    @Autowired
    private ActivityService activityService;

    /**
     * @title 搜索活动
     * @description 使用es进行全文检索
     * @author lidongming
     * @updateTime 2020/4/4 5:52
     */
    @Action(name = "搜索活动")
    @GetMapping("/search")
    public JSONResult search(String keyword){
        return JSONResult.success(searchService.searchActivity(keyword));
    }

    @Override
    public void afterPropertiesSet(){
        searchService.init(activityService.selectActivityListByTime());
    }
}
