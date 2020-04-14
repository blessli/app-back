package com.ldm.search;

import com.ldm.aop.Action;
import com.ldm.service.ActivityService;
import com.ldm.search.SearchService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
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
    public JSONResult search(String keyword,int pageNum,int pageSize){
        log.debug("获取搜索词 {} 的第 {} 页", keyword, pageNum);
        return JSONResult.success(activityService.selectActivityListByEs(searchService.searchActivity(keyword, pageNum, pageSize)));
    }

    @Override
    public void afterPropertiesSet(){
        searchService.init(activityService.selectActivityListByTime(0,100000));
    }
}
