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
    /**
     * @title 发表动态
     * @description RabbitMQ异步处理feed流
     * @author lidongming
     * @updateTime 2020/4/7 13:44
     */
    public int publish(PublishDynamic request) {
        int ans = dynamicDao.publishDynamic(request);
        if (ans <= 0) {
            return ans;
        }
        Jedis jedis=jedisPool.getResource();
        // RabbitMQ异步处理
        mqSender.feedDynamicPublish(JsonUtil.beanToString(request));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"image",Arrays.asList(request.getImages().split(",")).get(0));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"userId",String.valueOf(request.getUserId()));
        CacheService.returnToPool(jedis);
        return ans;
    }

    /**
     * @title 获取好友动态
     * @description 从redis获取当前用户的feed流
     * @author lidongming
     * @updateTime 2020/4/7 2:44
     */
    public List<DynamicIndex> selectDynamicList(int userId, int pageNum, int pageSize) {
        Jedis jedis=jedisPool.getResource();
        Set<String> meFollowSet=jedis.smembers(RedisKeys.meFollow(userId));
        // 判断我关注的用户中是否存在大V,如果有则拉取大V的发feed合并到我的收feed中
        for (String string:meFollowSet){
            if (jedis.sismember(RedisKeys.bigV(),string)){
                Set<String> feedSend=jedis.smembers(RedisKeys.dynamicFeedSend(Integer.valueOf(string)));
                jedis.sadd(RedisKeys.dynamicFeedReceive(userId),feedSend.toArray(new String[feedSend.size()]));
            }
        }
        // 求两个集合的差集
        Set<String> set=jedis.sdiff(RedisKeys.dynamicFeedReceive(userId),RedisKeys.deletedDynamic());
        List<Integer> dynamicIdList=new ArrayList<>(set.size());
        for(String string:set){
            dynamicIdList.add(Integer.valueOf(string));
        }
        List<DynamicIndex> dynamicIndexList = dynamicDao.selectDynamicList(dynamicIdList,pageNum*pageSize, pageSize);
        for (DynamicIndex dynamicIndex : dynamicIndexList) {
            List<String> list = Arrays.asList(dynamicIndex.getImages().split(","));
            dynamicIndex.setImageList(list);
            // 用户是否点赞,从redis中读取,true为已赞,false为未赞
            dynamicIndex.setIsLike(jedis.sismember(RedisKeys.likeDynamic(dynamicIndex.getDynamicId()),""+userId));
        }
        return dynamicIndexList;
    }

    /**
     * @title 获取我的动态列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 16:49
     */
    public List<DynamicIndex> selectMyDynamicList(int userId, int pageNum, int pageSize) {
        Jedis jedis=jedisPool.getResource();
        List<DynamicIndex> dynamicIndexList = dynamicDao.selectDynamicCreatedByMeList(userId, pageNum*pageSize, pageSize);
        for (DynamicIndex dynamicIndex : dynamicIndexList) {
            dynamicIndex.setImageList(Arrays.asList(dynamicIndex.getImages().split(",")));
            dynamicIndex.setAvatar(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"avatar"));
            dynamicIndex.setUserNickname(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"userNickname"));
            dynamicIndex.setIsLike(jedis.sismember(RedisKeys.likeDynamic(dynamicIndex.getUserId()),""+userId));
        }
        CacheService.returnToPool(jedis);
        return dynamicIndexList;
    }

    /**
     * @title 获取某个动态详情
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 20:57
     */
    public DynamicDetail selectDynamicDetail(int dynamicId, int userId) {
        Jedis jedis=jedisPool.getResource();
        DynamicDetail dynamicDetail = dynamicDao.selectDynamicDetail(dynamicId);
        dynamicDetail.setIsLike(jedis.sismember(RedisKeys.likeDynamic(dynamicId),""+userId));
        dynamicDetail.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
        dynamicDetail.setUserNickname(jedis.hget(RedisKeys.userInfo(userId),"userNickname"));
        dynamicDetail.setImageList(Arrays.asList(dynamicDetail.getImages().split(",")));
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
        Jedis jedis=jedisPool.getResource();
        jedis.del(RedisKeys.dynamicInfo(dynamicId));
        Set<String> set=jedis.smembers(RedisKeys.allDynamic(dynamicId));
        jedis.del(set.toArray(new String[set.size()]));
        jedis.sadd(RedisKeys.deletedDynamic(),String.valueOf(dynamicId));// 保存被删除的动态
        CacheService.returnToPool(jedis);
        return ans;
    }

    /**
     * @title 取消/点赞动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    public int likeDynamic(int dynamicId, int userId) {
        Jedis jedis=jedisPool.getResource();
        if (jedis.sismember(RedisKeys.likeDynamic(dynamicId),""+userId)){
            log.debug("用户 {} 取消给动态 {} 点赞", userId, dynamicId);
            jedis.srem(RedisKeys.likeDynamic(dynamicId),""+userId);
        }else{
            log.debug("用户 {} 给动态 {} 点赞", userId, dynamicId);
            jedis.sadd(RedisKeys.likeDynamic(dynamicId),""+userId);
        }
        CacheService.returnToPool(jedis);
        return 1;
    }
}
