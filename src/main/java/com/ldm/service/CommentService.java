package com.ldm.service;

import com.ldm.dao.CommentDao;
import com.ldm.entity.Comment;
import com.ldm.entity.ReplyNotice;
import com.ldm.entity.Reply;
import com.ldm.netty.SocketClientComponent;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import com.ldm.util.DateHandle;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidongming
 * @ClassName CommentService.java
 * @Description 评论服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private CommonService commonService;

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming
     * @updateTime 2020/4/6 19:29
     */
    public List<Comment> getCommentList(int itemId, int flag,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<Comment> commentList=commentDao.selectCommentList(itemId, flag, pageNum*pageSize, pageSize);
        for (Comment comment:commentList){
            comment.setAvatar(jedis.hget(RedisKeys.userInfo(comment.getUserId()),"avatar"));
            comment.setUserNickname(jedis.hget(RedisKeys.userInfo(comment.getUserId()),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return commentList;
    }
    /**
     * @title 发表评论
     * @description 更新activityScore
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int publishComment(PublishComment request) throws ParseException {
        Jedis jedis=jedisPool.getResource();
        if (request.getFlag()==0){
            int activityId=request.getItemId();
            commentDao.addActivityCommentCount(activityId);
            Integer commentCount= Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"commentCount"));
            commentCount++;
            jedis.hset(RedisKeys.activityInfo(activityId),"commentCount",""+commentCount);
            String publishTime=jedis.hget(RedisKeys.activityInfo(activityId),"publishTime");
            Integer viewCount= Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"viewCount"));
            Integer shareCount= Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"shareCount"));
            // 异步更新score
            commonService.updateActivityScore(activityId,publishTime,viewCount,commentCount,shareCount);
        }else {
            commentDao.addDynamicCommentCount(request.getItemId());
        }
        return commentDao.publishComment(request);
    }

    /**
     * @title 删除评论
     * @description 使用分布式锁和事务,flag为0则活动，flag为1则动态
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    @Transactional
    public int deleteComment(int itemId,int flag,int commentId){
        if (flag==0){
            commentDao.reduceActivityCommentCount(itemId);
        }else {
            commentDao.reduceDynamicCommentCount(itemId);
        }
        return commentDao.deleteComment(commentId);
    }

}
