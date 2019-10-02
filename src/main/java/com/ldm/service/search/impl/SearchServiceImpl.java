package com.ldm.service.search.impl;

import com.ldm.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * key: activity:history:{userId}
 * key: activity:hot
 */
@Service(value = "/searchService")
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    private int capacity=10;
    @Override
    public List<String> selectHistorySearchList(String userId,String type) {
        String historyKey=type+":history:"+userId;
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet=redisTemplate.opsForZSet().reverseRangeWithScores(historyKey,0,capacity-1);
        Iterator iterator=typedTupleSet.iterator();
        List<String> historyList=new ArrayList<>(capacity);
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> typedTuple = (ZSetOperations.TypedTuple<Object>) iterator.next();
            historyList.add((String) typedTuple.getValue());
        }
        return historyList;
    }

    @Override
    public List<String> selectHotSearchList(String type) {
        String hotKey=type+":hot";
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet=redisTemplate.opsForZSet().reverseRangeWithScores(hotKey,0,capacity-1);
        Iterator iterator=typedTupleSet.iterator();
        List<String> hotList=new ArrayList<>(capacity);
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> typedTuple = (ZSetOperations.TypedTuple<Object>) iterator.next();
            hotList.add((String) typedTuple.getValue());
        }
        return hotList;
    }

    @Override
    public void updateHistorySearch(String userId, String keyword,String type) {
        long now=System.currentTimeMillis();
        double score=now;
        String historyKey=type+":history:"+userId;
        String hotKey=type+":hot";
        redisTemplate.opsForZSet().add(historyKey,keyword,score);
        redisTemplate.opsForZSet().incrementScore(hotKey,keyword,1.0);
        if (redisTemplate.opsForZSet().size(historyKey)>capacity){
            redisTemplate.opsForZSet().removeRange(historyKey,0,0);
        }
    }
}
