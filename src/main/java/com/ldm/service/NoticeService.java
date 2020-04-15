package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.dao.DynamicDao;
import com.ldm.entity.ActivityApply;
import com.ldm.entity.CommentNotice;
import com.ldm.entity.LikeNotice;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
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
    private ActivityDao activityDao;

    @Autowired
    private DynamicDao dynamicDao;
    /**
     * @title 获取该用户发表的活动接收到的申请通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/12 0:27 
     */
    public List<ActivityApply> selectActivityApplyList(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();

        return activityDao.selectActivityApplyList(userId,pageNum*pageSize,pageSize);
    }

    /**
     * @title 获取点赞通知列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/11 22:38
     */
    public List<LikeNotice> selectLikeNotice(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        // 点赞未读数清零
        jedis.set(RedisKeys.commentNoticeUnread(1,userId),"0");
        CacheService.returnToPool(jedis);
        return dynamicDao.selectLikeNotice(userId,pageNum*pageSize,pageSize);
    }

    /**
     * @title 获取评论通知
     * @description 活动/动态/回复
     * @author lidongming
     * @updateTime 2020/4/11 14:46
     */
    public List<CommentNotice> selectCommentNotice(int userId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<String> list=jedis.lrange(RedisKeys.commentNotice(userId),pageNum*pageSize,pageSize);
        List<CommentNotice> commentNoticeList=new ArrayList<>();
        for(String string:list){
            CommentNotice commentNotice= JsonUtil.stringToBean(string,CommentNotice.class);
            // 还有一些赋值undo
            if (commentNotice.getFlag()==0){
                commentNotice.setImage(jedis.hget(RedisKeys.activityInfo(commentNotice.getItemId()),"image"));
            }else {
                commentNotice.setImage(jedis.hget(RedisKeys.dynamicInfo(commentNotice.getItemId()),"image"));
            }
            commentNotice.setAvatar(jedis.hget(RedisKeys.userInfo(commentNotice.getUserId()),"avatar"));
            commentNotice.setUserNickname(jedis.hget(RedisKeys.userInfo(commentNotice.getUserId()),"userNickname"));
        }
        // 评论通知未读数清零
        jedis.set(RedisKeys.commentNoticeUnread(2,userId),"0");
        CacheService.returnToPool(jedis);
        return commentNoticeList;
    }

}
