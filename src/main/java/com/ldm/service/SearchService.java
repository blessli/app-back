package com.ldm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ldm.config.EsConfig;
import com.ldm.dao.SearchActivityDao;
import com.ldm.entity.SearchDomain;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class SearchService {
    @Autowired
    private SearchActivityDao searchActivityDao;
    @Autowired
    private EsConfig esConfig;
    /**
     * @title 在es中搜索活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/6 16:25
     */
    public List<SearchDomain> searchActivity(String key) {
        Client client = esConfig.esTemplate();
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(key,"activityName","locationName","userNickname"));

        //搜索数据
        SearchResponse response = client.prepareSearch("app")
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.getTotalHits());
        List<SearchDomain> list = new ArrayList<>();
        for(SearchHit hit : searchHits) {
            SearchDomain entity = new SearchDomain();
            Map<String, Object> entityMap = hit.getSourceAsMap();

            //map to object
            if(!CollectionUtils.isEmpty(entityMap)) {
                if(!StringUtils.isEmpty(entityMap.get("activityId"))) {
                    entity.setActivityId(Integer.valueOf(String.valueOf(entityMap.get("activityId"))));
                }
                if(!StringUtils.isEmpty(entityMap.get("userId"))) {
                    entity.setUserId(Integer.valueOf(String.valueOf(entityMap.get("userId"))));
                }
                if(!StringUtils.isEmpty(entityMap.get("activityName"))) {

                    entity.setActivityName(String.valueOf(entityMap.get("activityName")));
                }
                if (!StringUtils.isEmpty(entityMap.get("activityType"))){
                    entity.setActivityType(String.valueOf(entityMap.get("activityType")));
                }
                if (!StringUtils.isEmpty(entityMap.get("locationName"))){
                    entity.setLocationName(String.valueOf(entityMap.get("locationName")));
                }
                if(!StringUtils.isEmpty(entityMap.get("publishTime"))) {
                    entity.setPublishTime(String.valueOf(entityMap.get("publishTime")));
                }
                if(!StringUtils.isEmpty(entityMap.get("beginTime"))) {
                    entity.setBeginTime(String.valueOf(entityMap.get("beginTime")));
                }
                if(!StringUtils.isEmpty(entityMap.get("endTime"))) {
                    entity.setEndTime(String.valueOf(entityMap.get("endTime")));
                }
                if(!StringUtils.isEmpty(entityMap.get("avatar"))) {
                    entity.setAvatar(String.valueOf(entityMap.get("avatar")));
                }
                if(!StringUtils.isEmpty(entityMap.get("userNickname"))) {
                    entity.setUserNickname(String.valueOf(entityMap.get("userNickname")));
                }
                if(!StringUtils.isEmpty(entityMap.get("viewCount"))) {
                    entity.setViewCount(Integer.valueOf(String.valueOf(entityMap.get("viewCount"))));
                }
                if(!StringUtils.isEmpty(entityMap.get("commentCount"))) {
                    entity.setCommentCount(Integer.valueOf(String.valueOf(entityMap.get("commentCount"))));
                }

            }
            list.add(entity);
        }
        return list;
    }
    /**
     * @title 保存活动到es
     * @description
     * @author lidongming
     * @updateTime 2020/4/6 16:20
     */
    public void saveActivity(SearchDomain searchDomain) {
        searchActivityDao.save(searchDomain);
    }
    /**
     * @title 删除es中的活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/6 16:21
     */
    public void deleteActivity(SearchDomain searchDomain) {
        searchActivityDao.delete(searchDomain);
    }
}

