package com.ldm.service.search.impl;

package com.ldm.search;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ldm.api.SearchService;
import com.ldm.config.EsConfig;
import com.ldm.dao.SearchDao;
import com.ldm.domain.LogDomain;
import com.ldm.domain.SearchActivityDomain;
import com.ldm.entity.search.SearchDomain;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchDao searchDao;
    @Autowired
    private SearchLogDao searchLogDao;
    @Autowired
    private EsConfig esConfig;
    @Override
    public PageInfo<SearchActivityDomain> searchActivity(int pageNum, int pageSize, String key) {
        System.out.println(pageNum+" "+pageSize+" "+key);
        PageHelper.startPage(pageNum, pageSize);
        Client client = esConfig.esTemplate();
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(key,"activityName","locationName","activityType"));

        //搜索数据
        SearchResponse response = client.prepareSearch("app")
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.getTotalHits());
        List<SearchDomain> list = new ArrayList<>();
        int ans=0;
        for(SearchHit hit : searchHits) {
            ans++;
            if(ans<=(pageNum-1)*pageSize) continue;
            if(ans>pageNum*pageSize) break;
            SearchDomain entity = new SearchDomain();
            Map<String, Object> entityMap = hit.getSourceAsMap();

            //map to object
            if(!CollectionUtils.isEmpty(entityMap)) {
                if(!StringUtils.isEmpty(entityMap.get("activityId"))) {
                    entity.setActivityId(Integer.valueOf(String.valueOf(entityMap.get("activityId"))));
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
                if(!StringUtils.isEmpty(entityMap.get("userId"))) {
                    entity.setUserId(Integer.valueOf(String.valueOf(entityMap.get("userId"))));
                }
            }
            list.add(entity);
        }
        PageInfo result = new PageInfo(list);
        return result;
    }

    @Override
    public void saveActivity(SearchActivityDomain searchActivityDomain) {
        searchDao.save(SearchDomain.transform(searchActivityDomain));
    }

    @Override
    public void deleteActivity(SearchActivityDomain searchActivityDomain) {
        searchDao.delete(SearchDomain.transform(searchActivityDomain));
    }

    @Override
    public void createLog(LogDomain log) {
        searchLogDao.save(SearchLogDomain.transform(log));
    }

    @Override
    public void updateLog(LogDomain log) {
        searchLogDao.delete(SearchLogDomain.transform(log));
        searchLogDao.save(SearchLogDomain.transform(log));
    }
    @Test
    public void test(){
        SearchDomain searchDomain=new SearchDomain();
        searchDomain.setLocationName("test");
        searchDomain.setActivityName("test");
        searchDomain.setActivityType("test");
        searchDomain.setUserId(1);
        searchDomain.setActivityId(1);
        searchDomain.setPublishTime("test");
        searchDao.save(searchDomain);
    }
}

