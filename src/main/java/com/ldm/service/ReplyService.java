package com.ldm.service;

import com.ldm.dao.ReplyDao;
import com.ldm.entity.Reply;
import com.ldm.entity.ReplyNotice;
import com.ldm.netty.SocketClientComponent;
import com.ldm.request.PublishReply;
import com.ldm.util.DateHandle;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidongming
 * @ClassName ReplyService.java
 * @Description 回复服务
 * @createTime 2020年04月16日 15:44:00
 */
@Service
public class ReplyService {
    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    @Autowired
    private SocketClientComponent socketClient;

    @Autowired
    private ReplyDao replyDao;

    /**
     * @title 获取回复列表
     * @description 活动详情页中点击某个评论展示回复列表
     * @author lidongming
     * @updateTime 2020/4/6 19:28
     */
    public List<Reply> getReplyList(int commentId, int pageNum, int pageSize){
        Jedis jedis=jedisPool.getResource();
        List<Reply> replyList=replyDao.selectReplyList(commentId, pageNum*pageSize, pageSize);
        for (Reply reply:replyList){
            reply.setAvatar(jedis.hget(RedisKeys.userInfo(reply.getFromUserId()),"avatar"));
            reply.setFromNickname(jedis.hget(RedisKeys.userInfo(reply.getFromUserId()),"userNickname"));
            reply.setToNickname(jedis.hget(RedisKeys.userInfo(reply.getToUserId()),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return replyList;
    }

    /**
     * @title 发表回复
     * @description redis存储这个回复信息,用于通知
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int publishReply(PublishReply request){
        Jedis jedis=jedisPool.getResource();
        int ans=replyDao.publishReply(request);
        if(ans<=0){
            return ans;
        }
        int toUserId=0;
        if (request.getFlag()==0){
            toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(request.getItemId()),"userId"));
        }else {
            toUserId= Integer.parseInt(jedis.hget(RedisKeys.dynamicInfo(request.getItemId()),"userId"));
        }
        jedis.incr(RedisKeys.noticeUnread(2,toUserId));
        Map<String,Object> map=new HashMap<>();
        map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,toUserId)));
        map.put("agreeCount",jedis.get(RedisKeys.noticeUnread(1,toUserId)));
        map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,toUserId)));
        map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,toUserId)));
        socketClient.send(String.valueOf(toUserId),"msgPage","notice",map);
        return ans;
    }
    /**
     * @title 删除回复
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:13
     */
    public int deleteReply(int commentId,int replyId){
        return replyDao.deleteReply(commentId,replyId);
    }
}
