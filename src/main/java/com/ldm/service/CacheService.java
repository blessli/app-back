package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.dao.DynamicDao;
import com.ldm.dao.NoticeDao;
import com.ldm.entity.*;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.util.*;

/**
 * @author lidongming
 * @ClassName CacheService.java
 * @Description 缓存服务
 * @createTime 2020年04月04日 05:05:00
 */
@Slf4j
@Service
public class CacheService {
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Autowired
    private JedisCluster jedis;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private DynamicDao dynamicDao;

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private FollowService followService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private ActivityService activityService;

//
//    /**
//     * 分布式锁
//     * @param key    键
//     * @param value  值
//     * @param nxxx NX|XX，NX=Only set the key if it does not already exist；XX=Only set the key if it already exist
//     * @param expx EX|PX，expire time units: EX = seconds; PX = milliseconds
//     * @param expireSeconds
//     * @param <T>
//     * @return
//     */
//    public <T> boolean lock(String key, T value, String nxxx, String expx, int expireSeconds) {
//        Jedis jedis = null;// redis连接
//        try {
//            jedis=jedisPool.getResource();
//            // 将对象转换为json字符串
//            String strValue = JsonUtil.beanToString(value);
//            if (strValue == null || strValue.length() <= 0){
//                return false;
//            }
//            if(jedis.set(key,strValue,nxxx,expx,expireSeconds)=="OK"){
//                return true;
//            }
//            return true;
//        }finally {
//            // 归还redis连接到连接池
//            returnToPool(jedis);
//        }
//
//    }
//    /**
//     * @title 解锁,使用lua脚本保证原子性
//     * @description
//     * @author lidongming
//     * @updateTime 2020/4/9 17:42
//     */
//    public Object unLock(String key){
//
//        Jedis jedis = null;// redis连接
//        try {
//            jedis=jedisPool.getResource();
//            //lua script
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            String request= UUID.randomUUID().toString();
//            Object result=jedis.eval(script, Collections.singletonList("lock-token"),Collections.singletonList("key"));
//            return result;// 1为成功
//        }finally {
//            // 归还redis连接到连接池
//            returnToPool(jedis);
//        }
//    }

    /**
     * @title 用户操作频率限制
     * @description 用于控制用户行为,如发布活动/动态/评论/回复/聊天
     * @author lidongming
     * @updateTime 2020/4/8 1:57
     */
    public boolean limitFrequency(String type,int userId){
        long nowTs=System.currentTimeMillis();
        int period=60,maxCount=10;
        String key= RedisKeys.limitFrequency(type, userId);
        jedis.zadd(key,nowTs,""+nowTs);
        jedis.zremrangeByScore(key,0,nowTs-period*1000);
        return jedis.zcard(key)>maxCount;
    }

    @Async("asyncServiceExecutor")
    public void init() {
        log.info("redis初始化开始!!!");
        List<SimpleUserInfo> simpleUserInfoList=userService.selectSimpleUserInfo();
        List<ActivityIndex> allActivity=activityDao.selectAllActivity();
        List<DynamicIndex> allDynamic =dynamicDao.selectAllDynamic();
        List<ActivityMember> allActivityView=activityDao.selectAllActivityView();
        List<ApplyNotice> applyNoticeList;
        List<LikeNotice> likeNoticeList;
        List<ReplyNotice> replyNoticeList;
        List<FollowNotice> followNoticeList;
//        Collection<JedisPool> jedisPools=jedis.getClusterNodes().values();
//        for (JedisPool jedisPool:jedisPools){
//            Jedis jedis1=jedisPool.getResource();
//            try {
//                jedis1.flushAll();
//            }finally {
//                jedis1.close();
//            }
//        }
        for(SimpleUserInfo simpleUserInfo:simpleUserInfoList){
            int userId=simpleUserInfo.getUserId();
            jedis.set(RedisKeys.firstToken(simpleUserInfo.getOpenId()),""+simpleUserInfo.getUserId());
            jedis.hset(RedisKeys.userInfo(userId),"avatar",simpleUserInfo.getAvatar());
            jedis.hset(RedisKeys.userInfo(userId),"userNickname",simpleUserInfo.getUserNickname());
            applyNoticeList=noticeDao.selectApplyNotice(userId,0,1000000);
            jedis.set(RedisKeys.noticeUnread(0,userId),String.valueOf(applyNoticeList.size()));
            likeNoticeList=noticeDao.selectLikeNotice(userId,0,1000000);
            jedis.set(RedisKeys.noticeUnread(1,userId),String.valueOf(likeNoticeList.size()));
            replyNoticeList=noticeDao.selectReplyNotice(userId,0,100000);
            jedis.set(RedisKeys.noticeUnread(2,userId),String.valueOf(replyNoticeList.size()));
            followNoticeList=noticeDao.selectFollowNotice(userId,0,100000);
            jedis.set(RedisKeys.noticeUnread(3,userId),String.valueOf(followNoticeList.size()));
            for (FollowNotice followNotice:followNoticeList){
                // 更新feed流,同步到redis
                followService.followUserSyncRedis(followNotice.getUserId(),userId);
            }
            for (LikeNotice likeNotice:likeNoticeList){
                // 点赞情况同步到redis
                jedis.sadd(RedisKeys.likeDynamic(likeNotice.getDynamicId()),""+likeNotice.getUserId());
            }
            for (ApplyNotice applyNotice:applyNoticeList){
                // 申请情况,同步到redis
                jedis.sadd(RedisKeys.activityJoined(applyNotice.getActivityId()),""+applyNotice.getUserId());
            }

        }
        for (ActivityMember activityView:allActivityView){
            jedis.sadd(RedisKeys.activityViewed(activityView.getActivityId()),""+activityView.getUserId());
        }
        for (ActivityIndex activity:allActivity){
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"userId",String.valueOf(activity.getUserId()));
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"image",Arrays.asList(activity.getImages().split(",")).get(0));
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"viewCount",""+activity.getViewCount());
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"commentCount",""+activity.getCommentCount());
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"shareCount",""+activity.getShareCount());
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"publishTime",activity.getPublishTime());
        }
        for (DynamicIndex dynamicIndex : allDynamic){
            jedis.sadd(RedisKeys.dynamicFeedSend(dynamicIndex.getUserId()),""+dynamicIndex.getDynamicId());
            jedis.hset(RedisKeys.dynamicInfo(dynamicIndex.getDynamicId()),"userId",String.valueOf(dynamicIndex.getUserId()));
            jedis.hset(RedisKeys.dynamicInfo(dynamicIndex.getDynamicId()),"image",Arrays.asList(dynamicIndex.getImages().split(",")).get(0));
        }
        log.info("redis初始化成功!!!");
    }
}