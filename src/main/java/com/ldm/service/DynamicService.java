package com.ldm.service;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.DynamicIndex;
import com.ldm.entity.DynamicDetail;
import com.ldm.rabbitmq.MQSender;
import com.ldm.request.PublishDynamic;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author lidongming
 * @ClassName DynamicService.java
 * @Description 动态服务
 * @createTime 2020年04月04日 05:05:00
 */
@Slf4j
@Service
public class DynamicService {

    @Autowired
    private DynamicDao dynamicDao;

    @Autowired
    private MQSender mqSender;
    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    // 发表动态
    public int publish(PublishDynamic request) {
        int ans = dynamicDao.publishDynamic(request);
        if (ans <= 0) {
            return ans;
        }
        Jedis jedis=jedisPool.getResource();
        // RabbitMQ异步处理feed流
        mqSender.feedDynamicPublish(JsonUtil.beanToString(request));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"image",Arrays.asList(request.getImages().split(",")).get(0));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"userId",String.valueOf(request.getUserId()));
        CacheService.returnToPool(jedis);
        return ans;
    }
    public List<DynamicIndex> common(List<DynamicIndex> dynamicIndexList, int userId,Jedis jedis) {
        for (DynamicIndex dynamicIndex : dynamicIndexList) {
            dynamicIndex.setAvatar(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"avatar"));
            dynamicIndex.setUserNickname(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"userNickname"));
            List<String> list = Arrays.asList(dynamicIndex.getImages().split(","));
            dynamicIndex.setImageList(list);
            dynamicIndex.setLikeCount(jedis.scard(RedisKeys.likeDynamic(dynamicIndex.getDynamicId())));
            // 用户是否点赞,从redis中读取,true为已赞,false为未赞
            dynamicIndex.setIsLike(jedis.sismember(RedisKeys.likeDynamic(dynamicIndex.getDynamicId()),""+userId));
        }
        return dynamicIndexList;
    }

    // 获取好友动态,从redis获取当前用户的feed流
    public List<DynamicIndex> selectDynamicList(int userId, int pageNum, int pageSize) {
        Jedis jedis=jedisPool.getResource();
        Set<String> meFollowSet=jedis.zrange(RedisKeys.meFollow(userId),0,-1);
        List<Integer> dynamicIdList=new ArrayList<>();
        Set<String> feedReceive=jedis.zrange(RedisKeys.dynamicFeedReceive(userId),0,-1);
        for (String str:feedReceive) {
            dynamicIdList.add(Integer.valueOf(str));
        }
        // 判断我关注的用户中是否存在大V,如果有则拉取大V的发feed合并到我的收feed中
        for (String str:meFollowSet){
            if (jedis.sismember(RedisKeys.bigV(),str)){
                Set<Tuple> feedSend=jedis.zrangeWithScores(RedisKeys.dynamicFeedSend(Integer.valueOf(str)),0,-1);
                for (Tuple tuple:feedSend) {
                    dynamicIdList.add(Integer.valueOf(tuple.getElement()));
                    jedis.zadd(RedisKeys.dynamicFeedReceive(userId),tuple.getScore(), tuple.getElement());
                }
            }
        }
        dynamicIdList.add(Integer.valueOf(0));
        log.info("dynamicIdList {} {}",dynamicIdList.toArray(),dynamicIdList.size());
        List<DynamicIndex> dynamicIndexList = dynamicDao.selectDynamicList(userId,dynamicIdList,pageNum*pageSize, pageSize);
        dynamicIndexList=common(dynamicIndexList,userId,jedis);
        return dynamicIndexList;
    }

    // 获取我的动态列表
    public List<DynamicIndex> selectMyDynamicList(int userId, int pageNum, int pageSize) {
        Jedis jedis=jedisPool.getResource();
        List<DynamicIndex> dynamicIndexList = dynamicDao.selectDynamicCreatedByMeList(userId, pageNum*pageSize, pageSize);
        dynamicIndexList=common(dynamicIndexList,userId,jedis);
        CacheService.returnToPool(jedis);
        return dynamicIndexList;
    }

    // 获取某个动态详情
    public DynamicDetail selectDynamicDetail(int dynamicId, int userId) {
        DynamicDetail dynamicDetail = dynamicDao.selectDynamicDetail(dynamicId);
        if (dynamicDetail.getStatus()==0) {
            return null;
        }
        Jedis jedis=jedisPool.getResource();
        dynamicDetail.setIsLike(jedis.sismember(RedisKeys.likeDynamic(dynamicId),""+userId));
        dynamicDetail.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
        dynamicDetail.setUserNickname(jedis.hget(RedisKeys.userInfo(userId),"userNickname"));
        dynamicDetail.setImageList(Arrays.asList(dynamicDetail.getImages().split(",")));
        dynamicDetail.setLikeCount(jedis.scard(RedisKeys.likeDynamic(dynamicId)));
        CacheService.returnToPool(jedis);
        return dynamicDetail;
    }

    /**
     * @title 删除动态
     * @description 使用分布式锁和事务,redis保存被删除的动态ID
     * @author lidongming
     * @updateTime 2020/4/7 13:45
     */
    @Transactional
    public int deleteDynamic(int dynamicId) {
        int ans = dynamicDao.deleteDynamic(dynamicId);
        if (ans <= 0) {
            return ans;
        }
//        Jedis jedis=jedisPool.getResource();
//        jedis.del(RedisKeys.dynamicInfo(dynamicId));
//        Set<String> set=jedis.smembers(RedisKeys.allDynamic(dynamicId));
//        jedis.del(set.toArray(new String[set.size()]));
//        jedis.sadd(RedisKeys.deletedDynamic(),String.valueOf(dynamicId));// 保存被删除的动态
//        CacheService.returnToPool(jedis);
        return ans;
    }

    /**
     * @title 取消/点赞动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    public int likeDynamic(int dynamicId, int userId) {
        String userIdStr=String.valueOf(userId);
        Jedis jedis= null;
        try {
            jedis=jedisPool.getResource();
            if (jedis.sismember(RedisKeys.likeDynamic(dynamicId),userIdStr)){
                log.info("用户 {} 取消给动态 {} 点赞", userId, dynamicId);
                jedis.srem(RedisKeys.likeDynamic(dynamicId),userIdStr);
            }else{
                log.info("用户 {} 给动态 {} 点赞", userId, dynamicId);
                jedis.sadd(RedisKeys.likeDynamic(dynamicId),userIdStr);
            }
        }finally {
            CacheService.returnToPool(jedis);
        }
        return 1;
    }
}
