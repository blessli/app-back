package com.ldm.service;

import com.ldm.config.EsConfig;
import com.ldm.dao.SearchActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.SearchDomain;
import com.ldm.request.PublishActivity;
import lombok.extern.slf4j.Slf4j;
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
import redis.clients.jedis.JedisPool;

import com.ldm.entity.SearchResult;

import java.util.*;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private SearchActivityDao searchActivityDao;
    @Autowired
    private EsConfig esConfig;

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    /**
     * @title 在es中搜索活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/6 16:25
     */
    public List<SearchResult> searchActivity(String key, int pageNum, int pageSize) {
        Client client = esConfig.esTemplate();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(key, "activityName", "locationName", "userNickname"));

        //搜索数据
        SearchResponse response = client.prepareSearch("app")
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.getTotalHits());
        List<SearchResult> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            SearchResult entity = new SearchResult();
            Map<String, Object> entityMap = hit.getSourceAsMap();

            //map to object
            if (!CollectionUtils.isEmpty(entityMap)) {
                if (!StringUtils.isEmpty(entityMap.get("activityId"))) {
                    entity.setActivityId(Integer.valueOf(String.valueOf(entityMap.get("activityId"))));
                }
                if (!StringUtils.isEmpty(entityMap.get("userId"))) {
                    entity.setUserId(Integer.valueOf(String.valueOf(entityMap.get("userId"))));
                }
                if (!StringUtils.isEmpty(entityMap.get("activityName"))) {

                    entity.setActivityName(String.valueOf(entityMap.get("activityName")));
                }
                if (!StringUtils.isEmpty(entityMap.get("activityType"))) {
                    entity.setActivityType(String.valueOf(entityMap.get("activityType")));
                }
                if (!StringUtils.isEmpty(entityMap.get("locationName"))) {
                    entity.setLocationName(String.valueOf(entityMap.get("locationName")));
                }
                if (!StringUtils.isEmpty(entityMap.get("publishTime"))) {
                    entity.setPublishTime(String.valueOf(entityMap.get("publishTime")));
                }
                if (!StringUtils.isEmpty(entityMap.get("beginTime"))) {
                    entity.setBeginTime(String.valueOf(entityMap.get("beginTime")));
                }
                if (!StringUtils.isEmpty(entityMap.get("endTime"))) {
                    entity.setEndTime(String.valueOf(entityMap.get("endTime")));
                }
                if (!StringUtils.isEmpty(entityMap.get("avatar"))) {
                    entity.setAvatar(String.valueOf(entityMap.get("avatar")));
                }
                if (!StringUtils.isEmpty(entityMap.get("userNickname"))) {
                    entity.setUserNickname(String.valueOf(entityMap.get("userNickname")));
                }
                if (!StringUtils.isEmpty(entityMap.get("viewCount"))) {
                    entity.setViewCount(Integer.valueOf(String.valueOf(entityMap.get("viewCount"))));
                }
                if (!StringUtils.isEmpty(entityMap.get("commentCount"))) {
                    entity.setCommentCount(Integer.valueOf(String.valueOf(entityMap.get("commentCount"))));
                }
                if (!StringUtils.isEmpty(entityMap.get("images"))) {
                    entity.setImageList(Arrays.asList(String.valueOf(entityMap.get("images")).split(",")));
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
    public void saveActivity(PublishActivity publishActivity) {
        SearchDomain searchDomain = new SearchDomain();
        searchActivityDao.save(searchDomain);
    }

    /**
     * @title 删除es中的活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/6 16:21
     */
    public void deleteActivity(int activityId) {
        searchActivityDao.deleteByActivityIdEquals(activityId);
    }

    public SearchDomain change(PublishActivity publishActivity) {
        SearchDomain searchDomain = new SearchDomain();
        searchDomain.setCommentCount(0);
        searchDomain.setViewCount(0);
        searchDomain.setUserId(publishActivity.getUserId());
        searchDomain.setBeginTime(publishActivity.getBeginTime());
        searchDomain.setEndTime(publishActivity.getEndTime());
        searchDomain.setActivityId(publishActivity.getActivityId());
        searchDomain.setActivityType(publishActivity.getActivityType());
        searchDomain.setActivityName(publishActivity.getActivityName());
        return searchDomain;
    }

    public void init(List<Activity> activityList) {
        Iterable<SearchDomain> iterable = searchActivityDao.findAll();
        Iterator it = iterable.iterator();
        while (it.hasNext()) {
            searchActivityDao.delete((SearchDomain) it.next());
        }
        for (Activity activity : activityList) {
            SearchDomain searchDomain = new SearchDomain();
            searchDomain.setActivityName(activity.getActivityName());
            searchDomain.setActivityType(activity.getActivityType());
            searchDomain.setActivityId(activity.getActivityId());
            searchDomain.setAvatar(activity.getAvatar());
            searchDomain.setEndTime(activity.getEndTime());
            searchDomain.setBeginTime(activity.getBeginTime());
            searchDomain.setUserId(activity.getUserId());
            searchDomain.setViewCount(activity.getViewCount());
            searchDomain.setCommentCount(activity.getCommentCount());
            searchDomain.setUserNickname(activity.getUserNickname());
            searchDomain.setLocationName(activity.getLocationName());
            searchDomain.setImages(activity.getImages());
            searchDomain.setPublishTime(activity.getPublishTime());
            searchActivityDao.save(searchDomain);
        }
        log.info("elasticsearch初始化完成");

    }
}

