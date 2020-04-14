package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.ActivityApply;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.MyActivity;
import com.ldm.netty.SocketClientComponent;
import com.ldm.request.PublishActivity;
import com.ldm.search.SearchService;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
    private SocketClientComponent socketClientComponent;

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
    public int publishActivity(PublishActivity request){
        int ans=activityDao.publishActivity(request);
        if (ans<=0) {
            return ans;
        }
        log.debug("发布活动成功,活动ID为 "+request.getActivityId());
        searchService.saveActivity(request);
        List<String> imageList= Arrays.asList(request.getImages().split(","));
        Jedis jedis=jedisPool.getResource();
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"userId",String.valueOf(request.getUserId()));
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"image",imageList.get(0));
        returnToPool(jedis);
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
        Jedis jedis=jedisPool.getResource();
        Set<String> set=jedis.smembers("activity:"+activityId);
        jedis.del(set.toArray(new String[set.size()]));
        jedis.del(RedisKeys.activityInfo(activityId));
        searchService.deleteActivity(activityId);
        returnToPool(jedis);
        return ans;
    }

    /**
     * 获取最新发布的活动
     * @return
     */
    public List<Activity> selectActivityListByTime(int pageNum,int pageSize){
        List<Activity> activityList=activityDao.selectActivityListByTime(pageNum*pageSize, pageSize);
        for(Activity activity:activityList){
            List<String> list= Arrays.asList(activity.getImages().split(","));
            activity.setImageList(list);
        }
        return activityList;
    }

    /**
     * @title 获取该活动的详情内容
     * @description 先走redis,redis没有再走mysql
     * @author lidongming 
     * @updateTime 2020/4/4 5:01 
     */
    public ActivityDetail selectActivityDetail(int activityId,int userId,int pageNum,int pageSize){
        clickActivityDetail(activityId, userId);
        ActivityDetail activityDetail=activityDao.selectActivityDetail(activityId);
        List<String> list=Arrays.asList(activityDetail.getImages().split(","));
        activityDetail.setImageList(list);
        activityDetail.setActivityCommentList(commentService.getCommentList(activityId,0,pageNum*pageSize,pageSize));
        return activityDetail;
    }

    /**
     * @title 获取我发布的活动列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 20:59
     */
    public List<Activity> selectActivityCreatedByMe(int userId,int pageNum,int pageSize){
        List<Activity> activityList=activityDao.selectActivityCreatedByMe(userId,pageNum*pageSize,pageSize);
        for (Activity activity:activityList){
            List<String> list= Arrays.asList(activity.getImages().split(","));
            activity.setImageList(list);
        }
        return activityList;
    }

    /**
     * @title 获取该用户申请加入的活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 21:14
     */
    public List<MyActivity> selectMyActivityList(int userId,int pageNum,int pageSize){
        List<MyActivity> myActivityList=activityDao.selectMyActivityList(userId, pageNum*pageSize, pageSize);
        for (MyActivity myActivity:myActivityList){
            List<String> list= Arrays.asList(myActivity.getImage().split(","));
            myActivity.setImage(list.get(0));
        }
        return myActivityList;
    }
    /**
     * 用户首次进入活动详情页，浏览量+1(先从redis判断)
     * @param activityId
     */
    public void clickActivityDetail(int activityId,int userId){
        String key="click:detail:"+activityId+":"+userId;
        Jedis jedis=jedisPool.getResource();
        jedis.sadd("activity:"+activityId,key);// 方便为了清理
        if (!jedis.exists(key)&& activityDao.isFirstClickActivity(activityId, userId)==0){
            activityDao.addViewCount(activityId, userId);
            jedis.set(key,"0");
        }
        returnToPool(jedis);
    }

    /**
     * @title 用户申请加入活动
     * @description 消息页的申请通知未读数+1,并推送过去
     * @author lidongming
     * @updateTime 2020/4/11 21:53
     */
    public int tryJoinActivity(int activityId,int userId){
        int ans=activityDao.tryJoinActivity(activityId, userId);
        if (ans<=0) {
            return ans;
        }
        Jedis jedis=jedisPool.getResource();
        int toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(activityId),"userId"));
        jedis.incr(RedisKeys.commentNoticeUnread(0,toUserId));
        Map<String,Object> map=new HashMap<>();
        map.put("applyCount",jedis.get(RedisKeys.commentNoticeUnread(0,toUserId)));
        map.put("agreeCount",jedis.get(RedisKeys.commentNoticeUnread(1,toUserId)));
        map.put("replyCount",jedis.get(RedisKeys.commentNoticeUnread(2,toUserId)));
        map.put("followCount",jedis.get(RedisKeys.commentNoticeUnread(3,toUserId)));
        socketClientComponent.send(String.valueOf(toUserId),"msgPage","notice",map);
        returnToPool(jedis);
        return ans;
    }

    /**
     * @title 用户取消加入活动
     * @description 消息页的申请通知未读数-1
     * @author lidongming
     * @updateTime 2020/4/11 21:53
     */
    public int cancelJoinActivity(int activityId,int userId){
        int ans=activityDao.cancelJoinActivity(activityId, userId);
        if(ans<=0) {
            return ans;
        }
        Jedis jedis=jedisPool.getResource();
        int toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(activityId),"userId"));
        jedis.decr(RedisKeys.commentNoticeUnread(0,toUserId));
        Map<String,Object> map=new HashMap<>();
        map.put("applyCount",jedis.get(RedisKeys.commentNoticeUnread(0,toUserId)));
        map.put("agreeCount",jedis.get(RedisKeys.commentNoticeUnread(1,toUserId)));
        map.put("replyCount",jedis.get(RedisKeys.commentNoticeUnread(2,toUserId)));
        map.put("followCount",jedis.get(RedisKeys.commentNoticeUnread(3,toUserId)));
        socketClientComponent.send(String.valueOf(toUserId),"msgPage","notice",map);
        returnToPool(jedis);
        return ans;
    }

    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int agreeJoinActivity(int activityId,int userId){
        return activityDao.agreeJoinActivity(activityId, userId);
    }

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int disagreeJoinActiviy(int activityId,int userId){
        return activityDao.disagreeJoinActivity(activityId, userId);
    }

    /**
     * @title 获取该用户发表的活动接收到的申请通知
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/12 0:27 
     */
    public List<ActivityApply> selectActivityApplyList(int userId,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();

        return activityDao.selectActivityApplyList(userId,pageNum*pageSize,pageSize);
    }

    public List<Activity> selectActivityListByEs(List<Integer> activityIdList){
        return activityDao.selectActivityListByEs(activityIdList);
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
