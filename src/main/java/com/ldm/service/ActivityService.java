package com.ldm.service;

import com.ldm.async.AsyncService;
import com.ldm.dao.ActivityDao;
import com.ldm.entity.*;
import com.ldm.netty.SocketClientComponent;
import com.ldm.request.PublishActivity;
import com.ldm.util.DateHandle;
import com.ldm.util.RedisKeys;
import com.ldm.util.TransactionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

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
    private TransactionHelper transactionHelper;

    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private CommentService commentService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private SocketClientComponent socketClient;

    @Autowired
    private JedisCluster jedis;

    /**
     * @title 获取获取列表-按时间排序
     * @description redis存储用户基本信息,用户是否浏览过该活动
     * @author lidongming
     * @updateTime 2020/4/15 15:09
     */
    public List<ActivityIndex> selectActivityListByTime(int userId,int pageNum,int pageSize){
        List<ActivityIndex> activityList=activityDao.selectActivityListByTime(pageNum*pageSize, pageSize);
        for(ActivityIndex activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
            /**
             * 查询当前用户是否浏览过该活动
             * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
             */
            activity.setIsViewed(isViewActivity(activity.getActivityId(),userId));
        }
        return activityList;
    }

    /**
     * @title 获取活动列表-按距离排序
     * @description
     * @author lidongming
     * @updateTime 2020/4/16 15:20
     */
    public List<ActivityIndex> selectActivityListByDistance(int userId,double longitude,double latitude,int pageNum,int pageSize){
        List<ActivityIndex> activityList= activityDao.selectActivityListByDistance(longitude, latitude, pageNum*pageSize, pageSize);
        for(ActivityIndex activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
            /**
             * 查询当前用户是否浏览过该活动
             * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
             */
            activity.setIsViewed(isViewActivity(activity.getActivityId(),userId));
        }
        return activityList;
    }

    /**
     * @title 获取活动列表-按热度排序
     * @description
     * @author lidongming
     * @updateTime 2020/4/17 21:16
     */
    public List<ActivityIndex> selectActivityListByHot(int userId,int pageNum,int pageSize){
        List<ActivityIndex> activityList= activityDao.selectActivityListByHot(pageNum*pageSize, pageSize);
        for(ActivityIndex activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
            /**
             * 查询当前用户是否浏览过该活动
             * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
             */
            activity.setIsViewed(isViewActivity(activity.getActivityId(),userId));
        }
        return activityList;
    }

    /**
     * @title 获取活动列表-按分类
     * @description
     * @author lidongming
     * @updateTime 2020/4/19 20:53
     */
    public List<ActivitySort> selectActivityListBySort(String activityType, int pageNum, int pageSize){
        List<ActivitySort> activitySortList=activityDao.selectActivityListBySort(activityType,pageNum*pageSize,pageSize);
        for (ActivitySort activitySort:activitySortList){
            activitySort.setImage(Arrays.asList(activitySort.getImages().split(",")).get(0));
        }
        return activitySortList;
    }

    /**
     * @title 推荐活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/17 21:20
     */
    public List<ActivityIndex> selectActivityListByRecommend(int userId,int pageNum,int pageSize){
        List<ActivityIndex> activityList= activityDao.selectActivityListByHot(pageNum*pageSize, pageSize);
        List<ActivityIndex> activityIndexList=new ArrayList<>();
        for(ActivityIndex activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
            /**
             * 查询当前用户是否浏览过该活动
             * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
             */
            if (!isViewActivity(activity.getActivityId(),userId)){
                activityIndexList.add(activity);
            }
        }
        return activityIndexList;
    }

    /**
     * @title 获取该活动的详情内容
     * @description 如果是第一次,则viewCount+1
     * @author lidongming
     * @updateTime 2020/4/4 5:01
     */
    public ActivityDetail selectActivityDetail(int activityId,int userId,int pageNum,int pageSize) throws ParseException {
        ActivityDetail activityDetail=activityDao.selectActivityDetail(activityId);
        if (!isViewActivity(activityId, userId)){
            if (transactionHelper.handleViewCount(activityId, userId)==1){
                // 异步更新score
                asyncService.updateActivityScore(activityId,activityDetail.getPublishTime(),activityDetail.getViewCount()+1,activityDetail.getCommentCount(),activityDetail.getShareCount());
            }
        }
        activityDetail.setImageList(Arrays.asList(activityDetail.getImages().split(",")));
        /**
         * 查询当前用户是否申请加入过该活动
         * 先从redis中查,redis没有再从mysql里查,mysql有的话,就同步到redis中
         */
        activityDetail.setIsJoined(isJoinActivity(activityId,userId));
        activityDetail.setAvatar(jedis.hget(RedisKeys.userInfo(activityDetail.getUserId()),"avatar"));
        activityDetail.setUserNickname(jedis.hget(RedisKeys.userInfo(activityDetail.getUserId()),"userNickname"));
        activityDetail.setActivityCommentList(commentService.getCommentList(activityId,0,pageNum*pageSize,pageSize));
        return activityDetail;
    }

    /**
     * @title 获取我发布的活动列表
     * @description 从redis中获取avatar,userNickname
     * @author lidongming
     * @updateTime 2020/4/10 20:59
     */
    public List<ActivityIndex> selectActivityCreatedByMe(int userId,int pageNum,int pageSize){
        List<ActivityIndex> activityList=activityDao.selectActivityCreatedByMe(userId,pageNum*pageSize,pageSize);
        for (ActivityIndex activity:activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
            activity.setAvatar(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"avatar"));
            activity.setUserNickname(jedis.hget(RedisKeys.userInfo(activity.getUserId()),"userNickname"));
        }
        return activityList;
    }

    /**
     * @title 获取申请加入的活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 21:14
     */
    public List<MyActivity> selectMyActivityList(int userId,int pageNum,int pageSize){
        List<MyActivity> myActivityList=activityDao.selectActivityApplyList(userId, pageNum*pageSize, pageSize);
        for (MyActivity myActivity:myActivityList){
            myActivity.setImage(Arrays.asList(myActivity.getImages().split(",")).get(0));
        }
        return myActivityList;
    }

    /**
     * @title 获取活动成员列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/18 14:25
     */
    public List<ActivityMember> selectActivityMemberList(int activityId){
        List<ActivityMember> activityMemberList=activityDao.selectActivityMemberList(activityId);
        for (ActivityMember member:activityMemberList){
            member.setAvatar(jedis.hget(RedisKeys.userInfo(member.getUserId()),"avatar"));
            member.setUserNickname(jedis.hget(RedisKeys.userInfo(member.getUserId()),"userNickname"));
        }
        return activityMemberList;
    }

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
        asyncService.updateActivityScore(request.getActivityId(),currDate,0,0,0);
        log.info("用户 {} 发布活动成功,活动ID为 {}",request.getUserId(),request.getActivityId());
        searchService.saveActivity(request);
        // redis保存活动基本信息
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"userId",""+request.getUserId());
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"image",Arrays.asList(request.getImages().split(",")).get(0));
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"viewCount","0");
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"commentCount","0");
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"shareCount","0");
        jedis.hset(RedisKeys.activityInfo(request.getActivityId()),"publishTime", currDate);
        return ans;
    }

    /**
     * @title 用户删除活动
     * @description 先删redis,再删db
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    @Transactional
    public int deleteActivity(int activityId){
        // 使用管道进行批量删除
//        Pipeline pipeline=jedis.pipelined();
        jedis.del(RedisKeys.activityInfo(activityId));
        jedis.del(RedisKeys.activityJoined(activityId));
        jedis.del(RedisKeys.activityViewed(activityId));
//        pipeline.sync();
        searchService.deleteActivity(activityId);
        return activityDao.deleteActivity(activityId);
    }



    /**
     * @title 用户取消/申请加入活动
     * @description 消息页的申请通知未读数+1,并推送过去
     * @author lidongming
     * @updateTime 2020/4/11 21:53
     */
    public int joinActivity(int activityId,int userId){
        int toUserId= getActivityUserId(activityId);
        if (toUserId==0){
            return 0;
        }
        int ans;
        Map<String,Object> map=new HashMap<>();
        // 用户已加入活动,再次点击就是取消加入
        if (isJoinActivity(activityId, userId)){
            log.info("用户 {} 取消加入活动 {}", userId, activityId);
            jedis.srem(RedisKeys.activityJoined(activityId),""+userId);
            ans=activityDao.cancelJoinActivity(activityId,userId);
        }else {
            log.info("用户 {} 申请加入活动 {}", userId, activityId);
            jedis.incr(RedisKeys.noticeUnread(0,toUserId));
            ans=activityDao.joinActivity(activityId,userId,toUserId);
        }
        // 当用户申请加入活动,活动发布者在线的话就会收到这个通知
        if (jedis.exists(RedisKeys.online(toUserId,"msgFlag"))){
            map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,toUserId)));
            map.put("likeCount",jedis.get(RedisKeys.noticeUnread(1,toUserId)));
            map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,toUserId)));
            map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,toUserId)));
            socketClient.send(String.valueOf(toUserId),"msgPage","notice",map);
        }
        return ans;
    }
    
    /**
     * @title 退出活动
     * @description 实现事务,先删redis,再操作db,保证一致性
     * @author lidongming 
     * @updateTime 2020/4/17 23:47 
     */
    @Transactional
    public int exitActivity(int activityId,int userId){
        jedis.srem(RedisKeys.activityJoined(activityId),""+userId);
        return activityDao.exitActivity(activityId, userId);
    }

    /**
     * @title 同意加入活动
     * @description 使用事务,使用lazy思想,当需要的时候,才同步到redis
     * @author lidongming
     * @updateTime 2020/4/18 14:36
     */
    @Transactional
    public int agreeJoinActivity(int activityId,int userId){
        return activityDao.agreeJoinActivity(activityId, userId);
    }

    /**
     * @title 拒绝该加入活动
     * @description 先删redis,后删db
     * @author lidongming
     * @updateTime 2020/4/18 14:36
     */
    public int disagreeJoinActivity(int activityId,int userId){
        jedis.srem(RedisKeys.activityJoined(activityId),""+userId);
        return activityDao.disagreeJoinActivity(activityId, userId);
    }

    public List<ActivityIndex> selectActivityListByEs(List<Integer> activityIdList){
        return activityDao.selectActivityListByEs(activityIdList);
    }

    /**
     * @title 分享活动
     * @description 更新activity的score
     * @author lidongming
     * @updateTime 2020/4/17 21:13
     */
    public int shareActivity(int activityId) throws ParseException {
        ScoreParameter scoreParameter=getScoreParameter(activityId);
        if (scoreParameter==null){
            return 0;
        }
        if (activityDao.shareActivity(activityId)<1){
            return 0;
        }
        int shareCount=scoreParameter.getShareCount()+1;
        jedis.hset(RedisKeys.activityInfo(activityId),"shareCount",""+shareCount);
        // 异步更新score
        asyncService.updateActivityScore(activityId,scoreParameter.getPublishTime(),scoreParameter.getViewCount(),scoreParameter.getCommentCount(),shareCount);
        return 1;
    }

    /**
     * 为了保证mysql与redis的数据一致性
     * 先查redis,redis没有的话查mysql,然后同步redis
     * @param activityId
     * @param userId
     * @return
     */
    public boolean isViewActivity(int activityId,int userId){
        if (jedis.sismember(RedisKeys.activityViewed(activityId),""+userId)){
            return true;
        }
        if (activityDao.isViewActivity(userId,activityId)!=null){
            jedis.sadd(RedisKeys.activityViewed(activityId),""+userId);
            return true;
        }
        return false;
    }

    public boolean isJoinActivity(int activityId,int userId){
        if (jedis.sismember(RedisKeys.activityJoined(activityId),""+userId)){
            return true;
        }
        if (activityDao.isJoinActivity(userId,activityId)!=null){
            jedis.sadd(RedisKeys.activityJoined(activityId),""+userId);
            return true;
        }
        return false;
    }

    public int getActivityUserId(int activityId){
        RedisUserId redisUserId;
        if (jedis.hexists(RedisKeys.activityInfo(activityId),"userId")){
            return Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"userId"));
        }else if ((redisUserId=activityDao.isExistActivity(activityId))!=null){
            jedis.hset(RedisKeys.activityInfo(activityId),"userId",""+redisUserId.getUserId());
            return redisUserId.getUserId();
        }
        return 0;
    }

    public ScoreParameter getScoreParameter(int activityId){
        ScoreParameter scoreParameter;
        if (jedis.exists(RedisKeys.activityInfo(activityId))){
            scoreParameter=new ScoreParameter();
            scoreParameter.setCommentCount(Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"commentCount")));
            scoreParameter.setPublishTime(jedis.hget(RedisKeys.activityInfo(activityId),"publishTime"));
            scoreParameter.setShareCount(Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"shareCount")));
            scoreParameter.setViewCount(Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"viewCount")));
            return scoreParameter;
        }else if((scoreParameter=activityDao.selectScoreParameter(activityId))!=null){
            jedis.hset(RedisKeys.activityInfo(activityId),"commentCount",""+scoreParameter.getCommentCount());
            jedis.hset(RedisKeys.activityInfo(activityId),"publishTime",scoreParameter.getPublishTime());
            jedis.hset(RedisKeys.activityInfo(activityId),"viewCount",""+scoreParameter.getViewCount());
            jedis.hset(RedisKeys.activityInfo(activityId),"shareCount",""+scoreParameter.getShareCount());
            return scoreParameter;
        }
        return null;
    }

}
