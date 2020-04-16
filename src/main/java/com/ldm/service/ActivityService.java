package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.MyActivity;
import com.ldm.netty.SocketClientComponent;
import com.ldm.request.PublishActivity;
import com.ldm.search.SearchService;
import com.ldm.util.DateHandle;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.util.*;

/**
 * @author lidongming
 * @ClassName ActivityService.java
 * @Description 活动服务
 * @createTime 2020年04月04日 05:05:00
 */
@Slf4j
@Service
public class ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private CommentService commentService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SocketClientComponent socketClient;

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;
    /**
     * @title 用户发布活动
     * @description 将发布的活动添加到es,redis中需要保存某些基本信息
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    public int publishActivity(PublishActivity request) throws ParseException {
        int ans=activityDao.publishActivity(request);
        if (ans<=0) {
            return ans;
        }
        String currDate=DateHandle.currentDate();
        // 异步更新score
        commonService.updateActivityScore(request.getActivityId(),currDate,0,0,0);
        log.debug("用户 {} 发布活动成功,活动ID为 {}",request.getUserId(),request.getActivityId());
        searchService.saveActivity(request);
        Jedis jedis=jedisPool.getResource();
        // redis保存活动基本信息
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"userId",""+request.getUserId());
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"viewCount","0");
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"commentCount","0");
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"shareCount","0");
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"publishTime", currDate);
        CacheService.returnToPool(jedis);
        return ans;
    }

    /**
     * @title 用户删除活动
     * @description 使用分布式锁和事务,删除活动需要删干净!!!
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    @Transactional
    public int deleteActivity(int activityId){
        int ans=activityDao.deleteActivity(activityId);
        if(ans<=0) {
            return ans;
        }
        log.debug("活动 {} 删除成功",activityId);
        Jedis jedis=jedisPool.getResource();
        Set<String> set=jedis.smembers(RedisKeys.allActivity(activityId));
        jedis.del(set.toArray(new String[set.size()]));// 删除所有与活动相关的key
        jedis.del(RedisKeys.activityInfo(activityId));
        searchService.deleteActivity(activityId);
        CacheService.returnToPool(jedis);
        return ans;
    }

    /**
     * @title 获取最新发布的活动
     * @description redis存储用户基本信息,用户是否浏览过该活动
     * @author lidongming
     * @updateTime 2020/4/15 15:09
     */
    public List<Activity> selectActivityListByTime(int userId,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<Activity> activityList=activityDao.selectActivityListByTime(pageNum*pageSize, pageSize);
        for(Activity activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
            // 当前用户是否浏览过该活动
            activity.setIsViewed(jedis.sismember(RedisKeys.activityViewed(activity.getActivityId()),""+userId));
        }
        CacheService.returnToPool(jedis);
        return activityList;
    }

    /**
     * @title 获取所有活动-按距离排序
     * @description
     * @author lidongming
     * @updateTime 2020/4/16 15:20
     */
    public List<Activity> selectActivityByDistance(int userId,double longitude,double latitude,int pageNum,int pageSize){
        return null;
    }

    /**
     * @title 获取该活动的详情内容
     * @description 如果是第一次,则viewCount+1
     * @author lidongming 
     * @updateTime 2020/4/4 5:01 
     */
    public ActivityDetail selectActivityDetail(int activityId,int userId,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();
        if (!jedis.sismember(RedisKeys.activityViewed(activityId),""+userId)){
            jedis.sadd(RedisKeys.activityViewed(activityId),""+userId);
            // 第一次浏览,则viewCount+1,并更新activityInfo
            jedis.hset(RedisKeys.activityViewed(activityId),"viewCount",""+jedis.scard(RedisKeys.activityViewed(activityId)));
            activityDao.addViewCount(activityId, userId);
        }
        ActivityDetail activityDetail=activityDao.selectActivityDetail(activityId);
        activityDetail.setImageList(Arrays.asList(activityDetail.getImages().split(",")));
        activityDetail.setIsJoined(jedis.sismember(RedisKeys.activityJoined(activityId),""+userId));
        activityDetail.setAvatar(jedis.hget(RedisKeys.userInfo(activityDetail.getUserId()),"avatar"));
        activityDetail.setUserNickname(jedis.hget(RedisKeys.userInfo(activityDetail.getUserId()),"userNickname"));
        activityDetail.setActivityCommentList(commentService.getCommentList(activityId,0,pageNum*pageSize,pageSize));
        CacheService.returnToPool(jedis);
        return activityDetail;
    }

    /**
     * @title 获取我发布的活动列表
     * @description 从redis中获取avatar,userNickname
     * @author lidongming
     * @updateTime 2020/4/10 20:59
     */
    public List<Activity> selectActivityCreatedByMe(int userId,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<Activity> activityList=activityDao.selectActivityCreatedByMe(userId,pageNum*pageSize,pageSize);
        for (Activity activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return activityList;
    }

    /**
     * @title 获取该用户申请加入的活动
     * @description 从redis中获取avatar,userNickname
     * @author lidongming
     * @updateTime 2020/4/10 21:14
     */
    public List<MyActivity> selectMyActivityList(int userId,int pageNum,int pageSize){
        List<MyActivity> myActivityList=activityDao.selectMyActivityList(userId, pageNum*pageSize, pageSize);
        for (MyActivity myActivity:myActivityList){
            myActivity.setImage(Arrays.asList(myActivity.getImage().split(",")).get(0));
        }
        return myActivityList;
    }

    /**
     * @title 用户申请加入活动
     * @description 消息页的申请通知未读数+1,并推送过去
     * @author lidongming
     * @updateTime 2020/4/11 21:53
     */
    public int joinActivity(int activityId,int userId){
        int ans=activityDao.tryJoinActivity(activityId, userId);
        if (ans<=0) {
            return ans;
        }
        Map<String,Object> map=new HashMap<>();
        Jedis jedis=jedisPool.getResource();
        int toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(activityId),"userId"));
        // 用户已加入活动,再次点击就是取消加入
        if (jedis.sismember(RedisKeys.activityJoined(activityId),""+userId)){
            log.debug("用户 {} 取消加入活动 {}", userId, activityId);
            jedis.decr(RedisKeys.noticeUnread(0,toUserId));
            jedis.srem(RedisKeys.activityJoined(activityId),""+userId);
        }else {
            log.debug("用户 {} 申请加入活动 {}", userId, activityId);
            jedis.incr(RedisKeys.noticeUnread(0,toUserId));
            jedis.sadd(RedisKeys.activityJoined(activityId),""+userId);
        }
        map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,toUserId)));
        map.put("agreeCount",jedis.get(RedisKeys.noticeUnread(1,toUserId)));
        map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,toUserId)));
        map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,toUserId)));
        socketClient.send(String.valueOf(toUserId),"msgPage","notice",map);
        CacheService.returnToPool(jedis);
        return ans;
    }
    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int agreeJoinActivity(int activityId,int userId){
        Jedis jedis=jedisPool.getResource();
        jedis.sadd(RedisKeys.activityJoined(activityId),""+userId);
        jedis.sadd(RedisKeys.activityByUserJoined(userId),""+activityId);
        CacheService.returnToPool(jedis);
        return activityDao.agreeJoinActivity(activityId, userId);
    }

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int disagreeJoinActivity(int activityId,int userId){
        Jedis jedis=jedisPool.getResource();
        jedis.srem(RedisKeys.activityJoined(activityId),""+userId);
        jedis.srem(RedisKeys.activityByUserJoined(userId),""+activityId);
        CacheService.returnToPool(jedis);
        return activityDao.disagreeJoinActivity(activityId, userId);
    }

    public List<Activity> selectActivityListByEs(List<Integer> activityIdList){
        return activityDao.selectActivityListByEs(activityIdList);
    }


}
