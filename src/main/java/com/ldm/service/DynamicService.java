package com.ldm.service;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.DynamicIndex;
import com.ldm.entity.DynamicDetail;
import com.ldm.entity.RedisUserId;
import com.ldm.netty.SocketClientComponent;
import com.ldm.rabbitmq.MQSender;
import com.ldm.request.PublishDynamic;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import java.text.ParseException;
import java.util.*;

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
    private SocketClientComponent socketClient;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private JedisCluster jedis;

    /**
     * @title 获取好友动态
     * @description 从redis获取当前用户的feed流
     * @author lidongming
     * @updateTime 2020/4/7 2:44
     */
    public List<DynamicIndex> selectDynamicList(int userId, int pageNum, int pageSize) {
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
        if (dynamicIdList.size()==0){
            return new ArrayList<>();
        }
        List<DynamicIndex> dynamicIndexList = dynamicDao.selectDynamicList(dynamicIdList,pageNum*pageSize, pageSize);
        for (DynamicIndex dynamicIndex : dynamicIndexList) {
            dynamicIndex.setAvatar(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"avatar"));
            dynamicIndex.setUserNickname(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"userNickname"));
            dynamicIndex.setImageList(Arrays.asList(dynamicIndex.getImages().split(",")));
            /**
             * 查询我是否点赞过该动态
             * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
             */
            dynamicIndex.setIsLike(isLikeDynamic(dynamicIndex.getDynamicId(),userId));
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
        List<DynamicIndex> dynamicIndexList = dynamicDao.selectDynamicCreatedByMeList(userId, pageNum*pageSize, pageSize);
        for (DynamicIndex dynamicIndex : dynamicIndexList) {
            dynamicIndex.setImageList(Arrays.asList(dynamicIndex.getImages().split(",")));
            dynamicIndex.setAvatar(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"avatar"));
            dynamicIndex.setUserNickname(jedis.hget(RedisKeys.userInfo(dynamicIndex.getUserId()),"userNickname"));
            /**
             * 查询我是否点赞过该动态
             * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
             */
            dynamicIndex.setIsLike(isLikeDynamic(dynamicIndex.getDynamicId(),userId));
        }
        return dynamicIndexList;
    }

    /**
     * @title 获取动态详情
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 20:57
     */
    public DynamicDetail selectDynamicDetail(int dynamicId, int userId) {
        DynamicDetail dynamicDetail = dynamicDao.selectDynamicDetail(dynamicId);
        /**
         * 查询当前用户是否点赞过该动态
         * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
         */
        dynamicDetail.setIsLike(isLikeDynamic(dynamicId, userId));
        dynamicDetail.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
        dynamicDetail.setUserNickname(jedis.hget(RedisKeys.userInfo(userId),"userNickname"));
        dynamicDetail.setImageList(Arrays.asList(dynamicDetail.getImages().split(",")));
        return dynamicDetail;
    }

    /**
     * @title 发表动态
     * @description RabbitMQ异步处理feed流
     * @author lidongming
     * @updateTime 2020/4/7 13:44
     */
    public int publish(PublishDynamic request) throws ParseException {
        int ans = dynamicDao.publishDynamic(request);
        if (ans <= 0) {
            return ans;
        }
        // RabbitMQ异步处理
        mqSender.feedDynamicPublish(JsonUtil.beanToString(request));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"image",Arrays.asList(request.getImages().split(",")).get(0));
        jedis.hset(RedisKeys.dynamicInfo(request.getDynamicId()),"userId",String.valueOf(request.getUserId()));
        return ans;
    }

    /**
     * @title 删除动态
     * @description 先删redis,再删db,redis保存被删除的动态ID
     * @author lidongming
     * @updateTime 2020/4/7 13:45
     */
    @Transactional
    public int deleteDynamic(int dynamicId) {
        // 使用管道进行批量删除
//        Pipeline pipeline=jedis.pipelined();
        jedis.del(RedisKeys.likeDynamic(dynamicId));
        jedis.del(RedisKeys.dynamicInfo(dynamicId));
//        pipeline.sync();
        return dynamicDao.deleteDynamic(dynamicId);
    }

    /**
     * @title 取消/点赞动态
     * @description 更新dynamic_score
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    public int likeDynamic(int dynamicId, int userId) {
        int toUserId=getDynamicUserId(dynamicId);
        if (toUserId==0) {
            return 0;
        }
        if (isLikeDynamic(dynamicId, userId)){
            log.info("用户 {} 取消给动态 {} 点赞", userId, dynamicId);
            jedis.srem(RedisKeys.likeDynamic(dynamicId),""+userId);
            return dynamicDao.cancelLikeDynamic(dynamicId, userId);
        }
        log.info("用户 {} 给动态 {} 点赞", userId, dynamicId);
        jedis.incr(RedisKeys.noticeUnread(1,toUserId));
        // 当用户给动态点赞,动态发布者在线的话就会收到这个通知
        if (jedis.exists(RedisKeys.online(toUserId,"msgFlag"))){
            Map<String,Object> map=new HashMap<>();
            map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,toUserId)));
            map.put("likeCount",jedis.get(RedisKeys.noticeUnread(1,toUserId)));
            map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,toUserId)));
            map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,toUserId)));
            socketClient.send(String.valueOf(toUserId),"msgPage","notice",map);
        }
        return dynamicDao.likeDynamic(dynamicId,userId,toUserId);
    }
    /**
     * 为了保证mysql与redis的数据一致性
     * 先查redis,redis没有的话查mysql,然后同步redis
     * @param dynamicId
     * @param userId
     * @return
     */
    public boolean isLikeDynamic(int dynamicId,int userId){
        if (jedis.sismember(RedisKeys.likeDynamic(dynamicId),""+userId)){
            return true;
        }
        if (dynamicDao.isLikeDynamic(userId,dynamicId)!=null){
            jedis.sadd(RedisKeys.likeDynamic(dynamicId),""+userId);
            return true;
        }
        return false;
    }
    public int getDynamicUserId(int dynamicId){
        RedisUserId redisUserId;
        if (jedis.hexists(RedisKeys.dynamicInfo(dynamicId),"userId")){
            return Integer.valueOf(jedis.hget(RedisKeys.dynamicInfo(dynamicId),"userId"));
        }else if ((redisUserId=dynamicDao.isExistDynamic(dynamicId))!=null){
            jedis.hset(RedisKeys.dynamicInfo(dynamicId),"userId",""+redisUserId.getUserId());
            return redisUserId.getUserId();
        }
        return 0;
    }
}
