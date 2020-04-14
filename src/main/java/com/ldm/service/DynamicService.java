package com.ldm.service;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.Dynamic;
import com.ldm.entity.DynamicDetail;
import com.ldm.entity.LikeNotice;
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
     * @description RabbitMQ处理feed流
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
        List<String> imageList= Arrays.asList(request.getImages().split(","));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"image",imageList.get(0));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"userId",String.valueOf(request.getUserId()));
        returnToPool(jedis);
        return ans;
    }

    /**
     * @title 获取已关注者发表的动态
     * @description 从redis获取当前用户的feed流
     * @author lidongming
     * @updateTime 2020/4/7 2:44
     */
    public List<Dynamic> selectDynamicList(int userId, int pageNum, int pageSize) {
        Jedis jedis=jedisPool.getResource();
        // 求两个集合的差集
        Set<String> set=jedis.sdiff(RedisKeys.dynamicFeedReceive(userId),RedisKeys.deletedDynamic());
        List<Integer> dynamicIdList=new ArrayList<>(set.size());
        for(String string:set){
            dynamicIdList.add(Integer.valueOf(string));
        }
        List<Dynamic> dynamicList = dynamicDao.selectDynamicList(dynamicIdList, pageNum*pageSize, pageSize);
        for (Dynamic dynamic : dynamicList) {
            List<String> list = Arrays.asList(dynamic.getImages().split(","));
            dynamic.setImageList(list);
            // 用户是否点赞,从redis中读取,1为已赞,0为未赞
            if (jedis.sismember(RedisKeys.likeDynamic(dynamic.getDynamicId()),""+userId)){
                dynamic.setIsLike(1);
            }else{
                dynamic.setIsLike(0);
            }
        }
        return dynamicList;
    }

    /**
     * @title 获取我的动态列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 16:49
     */
    public List<Dynamic> selectMyDynamicList(int userId, int pageNum, int pageSize) {
        List<Dynamic> dynamicList = dynamicDao.selectDynamicCreatedByMeList(userId, pageNum*pageSize, pageSize);
        for (Dynamic dynamic : dynamicList) {
            List<String> list = Arrays.asList(dynamic.getImages().split(","));
            dynamic.setImageList(list);
        }
        return dynamicList;
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
        List<String> list = Arrays.asList(dynamicDetail.getImages().split(","));
        dynamicDetail.setImageList(list);
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
        returnToPool(jedis);
        jedis.sadd(RedisKeys.deletedDynamic(),String.valueOf(dynamicId));// 保存被删除的动态
        return ans;
    }

    /**
     * @title 点赞或取消点赞动态
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
        returnToPool(jedis);
        return 1;
    }
    /**
     * @title 获取点赞通知列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/11 22:38
     */
    public List<LikeNotice> selectLikeNotice(int userId,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();
        // 点赞未读数清零
        jedis.set(RedisKeys.commentNoticeUnread(1,userId),"0");
        returnToPool(jedis);
        return dynamicDao.selectLikeNotice(userId,pageNum*pageSize,pageSize);
    }

    /**
     * @title 将redis连接对象归还到redis连接池
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 16:14
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null){
            jedis.close();
        }
    }
}
