package com.ldm.service;
import com.ldm.dao.NoticeDao;
import com.ldm.entity.*;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @author lidongming
 * @ClassName NoticeService.java
 * @Description 通知服务
 * @createTime 2020年04月15日 14:15:00
 */
@Slf4j
@Service
public class NoticeService {

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;
    
    @Autowired
    private NoticeDao noticeDao;
    /**
     * @title 获取申请通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/12 0:27 
     */
    public List<ApplyNotice> selectActivityApplyList(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<ApplyNotice> activityApplyList=noticeDao.selectApplyNotice(userId,pageNum*pageSize,pageSize);
        for (ApplyNotice activityApply:activityApplyList){
            activityApply.setAvatar(jedis.hget(RedisKeys.userInfo(activityApply.getUserId()),"avatar"));
            activityApply.setUserNickname(jedis.hget(RedisKeys.userInfo(activityApply.getUserId()),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return activityApplyList;
    }

    /**
     * @title 获取点赞通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/11 22:38
     */
    public List<LikeNotice> selectLikeNotice(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        // 点赞未读数清零
        jedis.set(RedisKeys.noticeUnread(1,userId),"0");
        List<LikeNotice> likeNoticeList=noticeDao.selectLikeNotice(userId,pageNum*pageSize,pageSize);
        for (LikeNotice likeNotice:likeNoticeList){
            likeNotice.setImage(jedis.hget(RedisKeys.dynamicInfo(likeNotice.getDynamicId()),"image"));
            likeNotice.setAvatar(jedis.hget(RedisKeys.userInfo(likeNotice.getUserId()),"avatar"));
            likeNotice.setUserNickname(jedis.hget(RedisKeys.userInfo(likeNotice.getUserId()),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return likeNoticeList;
    }

    /**
     * @title 获取回复通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/11 14:46
     */
    public List<ReplyNotice> selectReplyNotice(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<ReplyNotice> replyNoticeList=noticeDao.selectReplyNotice(userId, pageNum*pageSize, pageSize);
        for(ReplyNotice reply:replyNoticeList){
            // 还有一些赋值undo,toContent
            reply.setAvatar(jedis.hget(RedisKeys.userInfo(reply.getUserId()),"avatar"));
            reply.setUserNickname(jedis.hget(RedisKeys.userInfo(reply.getUserId()),"userNickname"));
        }
        // 回复通知未读数清零
        jedis.set(RedisKeys.noticeUnread(2,userId),"0");
        CacheService.returnToPool(jedis);
        return replyNoticeList;
    }

    /**
     * @title 获取关注通知
     * @description 显示是否互相关注
     * @author lidongming
     * @updateTime 2020/4/15 16:27
     */
    public List<FollowNotice> selectFollowNotice(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<FollowNotice> followNoticeList=noticeDao.selectFollowNotice(userId, pageNum*pageSize, pageSize);
        for (FollowNotice followNotice:followNoticeList){
            followNotice.setAvatar(jedis.hget(RedisKeys.userInfo(followNotice.getUserId()),"avatar"));
            followNotice.setUserNickname(jedis.hget(RedisKeys.userInfo(followNotice.getUserId()),"userNickname"));
            followNotice.setIsMutualFollow(jedis.sismember(RedisKeys.meFollow(userId),""+followNotice.getUserId()));
        }
        return followNoticeList;
    }

}
