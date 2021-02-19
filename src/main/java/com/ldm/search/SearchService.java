//package com.ldm.search;
//
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
////import com.ldm.config.EsConfig;
//import com.ldm.search.SearchActivityDao;
//import com.ldm.entity.ActivityIndex;
//import com.ldm.search.SearchDomain;
//import com.ldm.request.PublishActivity;
//import com.ldm.service.ActivityService;
//import com.ldm.service.CacheService;
//import com.ldm.util.RedisKeys;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//import java.util.*;
//
//@Slf4j
//@Service
//public class SearchService {
//    @Autowired
//    private SearchActivityDao searchActivityDao;
//
//    @Autowired
//    private ActivityService activityService;
////    @Autowired
////    private EsConfig esConfig;
//
//    /**
//     * 通过连接池对象可以获得对redis的连接
//     */
//    @Autowired
//    JedisPool jedisPool;
//
//    /**
//     * @title 在es中搜索活动
//     * @description
//     * @author lidongming
//     * @updateTime 2020/4/6 16:25
//     */
//    public List<ActivityIndex> searchActivity(int userId,String key, int pageNum, int pageSize) {
//        PageHelper.startPage(pageNum, pageSize);
//        Client client = esConfig.esTemplate();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(key, "activityName", "locationName", "activityType","userNickname"));
//
//        //搜索数据
//        SearchResponse response = client.prepareSearch("app")
//                .setQuery(boolQueryBuilder)
//                .execute().actionGet();
//        SearchHits searchHits = response.getHits();
//        System.out.println(searchHits.getTotalHits());
//        int ans=0;
//        List<Integer> activityIdList=new ArrayList<>();
//        for (SearchHit hit : searchHits) {
//            ans++;
//            if(ans<=(pageNum-1)*pageSize) {
//                continue;
//            }
//            if(ans>pageNum*pageSize){
//                break;
//            }
//            Map<String, Object> entityMap = hit.getSourceAsMap();
//
//            //map to object
//            if (!CollectionUtils.isEmpty(entityMap)) {
//                if (!StringUtils.isEmpty(entityMap.get("activityId"))) {
//                    activityIdList.add(Integer.valueOf(String.valueOf(entityMap.get("activityId"))));
//                }
//
//            }
//        }
//        Jedis jedis=jedisPool.getResource();
//        List<ActivityIndex> activityList=activityService.selectActivityListByEs(activityIdList);
//        for (ActivityIndex activity:activityList){
//            List<String> imageList= Arrays.asList(activity.getImages().split(","));
//            activity.setImageList(imageList);
//            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
//            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
//            // 当前用户是否浏览过该活动
//            activity.setIsViewed(jedis.sismember(RedisKeys.activityViewed(activity.getActivityId()),""+userId));
//        }
//        CacheService.returnToPool(jedis);
//        return activityList;
//    }
//
//    /**
//     * @title 保存活动到es
//     * @description
//     * @author lidongming
//     * @updateTime 2020/4/6 16:20
//     */
//    public void saveActivity(PublishActivity request) {
//        SearchDomain searchDomain = new SearchDomain();
//        searchDomain.setActivityId(request.getActivityId());
//        searchDomain.setActivityType(request.getActivityType());
//        searchDomain.setActivityName(request.getActivityName());
//        searchActivityDao.save(searchDomain);
//    }
//
//    /**
//     * @title 删除es中的活动
//     * @description
//     * @author lidongming
//     * @updateTime 2020/4/6 16:21
//     */
//    public void deleteActivity(int activityId) {
//        searchActivityDao.deleteByActivityIdEquals(activityId);
//    }
//
//    public void init(List<ActivityIndex> activityList) {
//        Iterable<SearchDomain> iterable = searchActivityDao.findAll();
//        Iterator it = iterable.iterator();
//        while (it.hasNext()) {
//            searchActivityDao.delete((SearchDomain) it.next());
//        }
//        for (ActivityIndex activity : activityList) {
//            SearchDomain searchDomain = new SearchDomain();
//            searchDomain.setActivityName(activity.getActivityName());
//            searchDomain.setActivityType(activity.getActivityType());
//            searchDomain.setActivityId(activity.getActivityId());
//            searchDomain.setUserNickname(activity.getUserNickname());
//            searchDomain.setLocationName(activity.getLocationName());
//            searchActivityDao.save(searchDomain);
//        }
//        log.info("elasticsearch初始化完成");
//
//    }
//}
//
