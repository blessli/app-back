package com.ldm.controller;

import com.ldm.service.SearchService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "搜索相关接口",description = "提供搜索相关的REST API")
@RestController
public class SearchController implements InitializingBean {
    @Autowired
    private SearchService searchService;

    /**
     * @title 搜索活动
     * @description 使用es进行全文检索
     * @author lidongming
     * @updateTime 2020/4/4 5:52
     */
    @GetMapping("/search")
    public JSONResult search(String keyword){
        return JSONResult.success();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
