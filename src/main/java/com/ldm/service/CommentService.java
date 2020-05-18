package com.ldm.service;

import com.ldm.async.AsyncService;
import com.ldm.dao.ActivityDao;
import com.ldm.dao.CommentDao;
import com.ldm.entity.Comment;
import com.ldm.entity.ReplyNotice;
import com.ldm.entity.Reply;
import com.ldm.entity.ScoreParameter;
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
import redis.clients.jedis.JedisCluster;
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
    private ActivityDao activityDao;
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private JedisCluster jedis;

    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming
     * @updateTime 2020/4/6 19:29
     */
    public List<Comment> getCommentList(int itemId, int flag,int pageNum,int pageSize){
        List<Comment> commentList=commentDao.selectCommentList(itemId, flag, pageNum*pageSize, pageSize);
        for (Comment comment:commentList){
            comment.setAvatar(jedis.hget(RedisKeys.userInfo(comment.getUserId()),"avatar"));
            comment.setUserNickname(jedis.hget(RedisKeys.userInfo(comment.getUserId()),"userNickname"));
        }
        return commentList;
    }
    /**
     * @title 发表评论
     * @description 更新activityScore
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    @Transactional
    public int publishComment(PublishComment request) throws ParseException {
        Jedis jedis=null;
        if (request.getFlag()==0){
            int activityId=request.getItemId();
            ScoreParameter scoreParameter=getScoreParameter(activityId);
            if (scoreParameter==null){
                return 0;
            }
            int commentCount=scoreParameter.getCommentCount()+1;
            jedis.hset(RedisKeys.activityInfo(activityId),"commentCount",""+commentCount);
            if (commentDao.addActivityCommentCount(activityId)==0){
                return 0;
            }
            // 异步更新score
            asyncService.updateActivityScore(activityId,scoreParameter.getPublishTime(),scoreParameter.getViewCount(),commentCount,scoreParameter.getShareCount());
        }else {
            int dynamicId=request.getItemId();
            commentDao.addDynamicCommentCount(dynamicId);
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
    public int deleteComment(int itemId,int flag,int commentId) throws ParseException {
        int activityId,dynamicId;
        if (flag==0){
            activityId=itemId;
            ScoreParameter scoreParameter=getScoreParameter(activityId);
            if (scoreParameter==null){
                return 0;
            }
            int commentCount= scoreParameter.getCommentCount()-1;
            jedis.hset(RedisKeys.activityInfo(activityId),"commentCount",""+commentCount);
            // 异步更新score
            asyncService.updateActivityScore(activityId,scoreParameter.getPublishTime(),scoreParameter.getViewCount(),commentCount,scoreParameter.getShareCount());
            commentDao.reduceActivityCommentCount(activityId);
        }else {
            dynamicId=itemId;
            commentDao.reduceDynamicCommentCount(dynamicId);
        }
        return commentDao.deleteComment(commentId);
    }

    public ScoreParameter getScoreParameter(int activityId){
        ScoreParameter scoreParameter;
        if (jedis.exists(RedisKeys.activityInfo(activityId))){
            scoreParameter=new ScoreParameter();
            Integer commentCount= Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"commentCount"));
            String publishTime=jedis.hget(RedisKeys.activityInfo(activityId),"publishTime");
            Integer viewCount= Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"viewCount"));
            Integer shareCount= Integer.valueOf(jedis.hget(RedisKeys.activityInfo(activityId),"shareCount"));
            scoreParameter.setCommentCount(commentCount);
            scoreParameter.setPublishTime(publishTime);
            scoreParameter.setShareCount(shareCount);
            scoreParameter.setViewCount(viewCount);
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
