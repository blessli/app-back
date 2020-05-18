package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.async.AsyncService;
import com.ldm.service.CacheService;
import com.ldm.service.SearchService;
import com.ldm.service.ActivityService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.text.ParseException;

@Slf4j
@RestController
@Validated
public class SearchController implements InitializingBean {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private CacheService cacheService;

    @Action(name = "搜索活动")
    @GetMapping("/search")
    public JSONResult search(@Valid @Min(1) int userId,@Valid @NotBlank String keyword){
        log.info("用户 {} 获取搜索 {}", userId,keyword);
        return JSONResult.success(searchService.searchActivity(userId, keyword));
    }

    @Override
    public void afterPropertiesSet() {
        cacheService.init();
        // 异步初始化es
        asyncService.initEs();
    }
}
