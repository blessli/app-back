package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.MyActivity;
import com.ldm.netty.SocketClientComponent;
import com.ldm.request.PublishActivity;
import com.ldm.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CacheService cacheService;

    @Autowired
    private SocketClientComponent socketClientComponent;
    /**
     * @title 用户发布活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    public int publishActivity(PublishActivity request){
        int ans=activityDao.publishActivity(request);
        if (ans<=0) {
            return ans;
        }
        log.info("发布活动成功,活动ID为 "+request.getActivityId());
        return ans;
    }

    /**
     * @title 用户删除活动
     * @description 使用分布式锁和事务
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    @Transactional
    public int deleteActivity(int activityId){
        cacheService.mdel("activity:"+activityId);
        return activityDao.deleteActivity(activityId);
    }

    /**
     * 获取最新发布的活动
     * @return
     */
    public List<Activity> selectActivityListByTime(){
        List<Activity> activityList=activityDao.selectActivityListByTime();
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
    public ActivityDetail selectActivityDetail(int activityId,int userId){
        clickActivityDetail(activityId, userId);
        ActivityDetail activityDetail=activityDao.selectActivityDetail(activityId, userId);
        List<String> list=Arrays.asList(activityDetail.getImages().split(","));
        activityDetail.setImageList(list);
        activityDetail.setActivityCommentList(commentService.getCommentList(activityId,0));
        return activityDetail;
    }

    /**
     * @title 获取我发布的活动列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 20:59
     */
    public List<Activity> selectActivityCreatedByMe(int userId){
        List<Activity> activityList=activityDao.selectActivityCreatedByMe(userId);
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
    public List<MyActivity> selectMyActivityList(int userId){
        List<MyActivity> myActivityList=activityDao.selectMyActivityList(userId);
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
        cacheService.sadd("activity:"+activityId,key);// 方便为了清理
        if (!cacheService.exists(key)&& activityDao.isFirstClickActivity(activityId, userId)==0){
            activityDao.addViewCount(activityId, userId);
            cacheService.set(key,0);
        }
    }

    /**
     * @title 用户申请加入活动
     * @description 消息页的申请通知未读数+1
     * @author lidongming
     * @updateTime 2020/4/11 21:53
     */
    public int tryJoinActivity(int activityId,int userId){
        int ans=activityDao.tryJoinActivity(activityId, userId);
        if(ans>0){
            int toUserId= Integer.parseInt(cacheService.hget(RedisKeyUtil.getActivityInfo(activityId),"userId"));
            cacheService.incr(RedisKeyUtil.getCommentNoticeUnread(0,toUserId));
            Map<String,Object> map=new HashMap<>();
            map.put("applyCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(0,toUserId),Integer.class));
            map.put("agreeCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(1,toUserId),Integer.class));
            map.put("replyCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(2,toUserId),Integer.class));
            map.put("followCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(3,toUserId),Integer.class));
            socketClientComponent.send(String.valueOf(toUserId),"msgPage","notice",map);
        }
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
        int toUserId= Integer.parseInt(cacheService.hget(RedisKeyUtil.getActivityInfo(activityId),"userId"));
        cacheService.decr(RedisKeyUtil.getCommentNoticeUnread(0,toUserId));
        Map<String,Object> map=new HashMap<>();
        map.put("applyCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(0,toUserId),Integer.class));
        map.put("agreeCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(1,toUserId),Integer.class));
        map.put("replyCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(2,toUserId),Integer.class));
        map.put("followCount",cacheService.get(RedisKeyUtil.getCommentNoticeUnread(3,toUserId),Integer.class));
        socketClientComponent.send(String.valueOf(toUserId),"msgPage","notice",map);
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

}
