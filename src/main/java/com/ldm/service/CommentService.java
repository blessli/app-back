package com.ldm.service;

import com.ldm.dao.CommentDao;
import com.ldm.entity.Comment;
import com.ldm.entity.CommentNotice;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidongming
 * @ClassName CommentService.java
 * @Description 评论，回复服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private SocketClientComponent socketClientComponent;

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;
    /**
     * @title 获取回复列表
     * @description 活动详情页中点击某个评论展示回复列表
     * @author lidongming
     * @updateTime 2020/4/6 19:28
     */
    public List<Reply> getReplyList(int commentId,int pageNum,int pageSize){
        return commentDao.selectReplyList(commentId, pageSize* (pageNum - 1), pageSize);
    }
    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming
     * @updateTime 2020/4/6 19:29
     */
    public List<Comment> getCommentList(int itemId, int flag,int pageNum,int pageSize){
        return commentDao.selectCommentList(itemId, flag, pageSize* (pageNum - 1), pageSize);
    }
    /**
     * @title 发表评论
     * @description redis存储这个评论信息,用于通知
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int publishComment(PublishComment request){
        Jedis jedis=jedisPool.getResource();
        if (request.getFlag()==0){
            commentDao.addActivityCommentCount(request.getItemId());
        }else {
            commentDao.addDynamicCommentCount(request.getItemId());
        }
        int ans=commentDao.publishComment(request);
        if(ans>0&&request.getToUserId()!=request.getUserId()){
            int toUserId=0;
            if (request.getFlag()==0){
                toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(request.getItemId()),"userId"));
            }else {
                toUserId= Integer.parseInt(jedis.hget(RedisKeys.dynamicInfo(request.getItemId()),"userId"));
            }
            jedis.incr(RedisKeys.commentNoticeUnread(2,toUserId));
            Map<String,Object> map=new HashMap<>();
            map.put("applyCount",jedis.get(RedisKeys.commentNoticeUnread(0,toUserId)));
            map.put("agreeCount",jedis.get(RedisKeys.commentNoticeUnread(1,toUserId)));
            map.put("replyCount",jedis.get(RedisKeys.commentNoticeUnread(2,toUserId)));
            map.put("followCount",jedis.get(RedisKeys.commentNoticeUnread(3,toUserId)));
            socketClientComponent.send(String.valueOf(toUserId),"msgPage","notice",map);
            CommentNotice commentNotice=new CommentNotice();
            commentNotice.setContent(request.getContent());
            commentNotice.setFlag(request.getFlag());
            commentNotice.setIsReply(0);
            commentNotice.setItemId(request.getItemId());
            commentNotice.setUserId(request.getUserId());
            commentNotice.setPublishTime(DateHandle.currentDate());
            jedis.lpush(RedisKeys.commentNotice(request.getToUserId()), JsonUtil.beanToString(commentNotice));
        }
        return ans;
    }
    /**
     * @title 发表回复
     * @description redis存储这个回复信息,用于通知
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int publishReply(PublishReply request){
        Jedis jedis=jedisPool.getResource();
        int ans=commentDao.publishReply(request);
        if(ans<=0){
            return ans;
        }
        int toUserId=0;
        if (request.getFlag()==0){
            toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(request.getItemId()),"userId"));
        }else {
            toUserId= Integer.parseInt(jedis.hget(RedisKeys.dynamicInfo(request.getItemId()),"userId"));
        }
        jedis.incr(RedisKeys.commentNoticeUnread(2,toUserId));
        Map<String,Object> map=new HashMap<>();
        map.put("applyCount",jedis.get(RedisKeys.commentNoticeUnread(0,toUserId)));
        map.put("agreeCount",jedis.get(RedisKeys.commentNoticeUnread(1,toUserId)));
        map.put("replyCount",jedis.get(RedisKeys.commentNoticeUnread(2,toUserId)));
        map.put("followCount",jedis.get(RedisKeys.commentNoticeUnread(3,toUserId)));
        socketClientComponent.send(String.valueOf(toUserId),"msgPage","notice",map);
        return ans;
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
    /**
     * @title 删除回复
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:13
     */
    public int deleteReply(int commentId,int replyId){
        return commentDao.deleteReply(commentId,replyId);
    }

    /**
     * @title 获取评论通知
     * @description 活动/动态/回复
     * @author lidongming
     * @updateTime 2020/4/11 14:46
     */
    public List<CommentNotice> selectCommentNotice(int userId,int pageNum,int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<String> list=jedis.lrange(RedisKeys.commentNotice(userId),pageNum,pageSize);
        List<CommentNotice> commentNoticeList=new ArrayList<>();
        for(String string:list){
            CommentNotice commentNotice=JsonUtil.stringToBean(string,CommentNotice.class);
            // 还有一些赋值undo
            if (commentNotice.getFlag()==0){
                commentNotice.setImage(jedis.hget(RedisKeys.activityInfo(commentNotice.getItemId()),"image"));
            }else {
                commentNotice.setImage(jedis.hget(RedisKeys.dynamicInfo(commentNotice.getItemId()),"image"));
            }
            commentNotice.setAvatar(jedis.hget(RedisKeys.userInfo(commentNotice.getUserId()),"avatar"));
            commentNotice.setUserNickname(jedis.hget(RedisKeys.userInfo(commentNotice.getUserId()),"userNickname"));
        }
        jedis.set(RedisKeys.commentNoticeUnread(2,userId),"0");
        returnToPool(jedis);
        return commentNoticeList;
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
