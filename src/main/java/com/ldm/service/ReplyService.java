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
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
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

    @Autowired
    private JedisCluster jedis;

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
        List<Reply> replyList=replyDao.selectReplyList(commentId, pageNum*pageSize, pageSize);
        for (Reply reply:replyList){
            reply.setAvatar(jedis.hget(RedisKeys.userInfo(reply.getFromUserId()),"avatar"));
            reply.setFromNickname(jedis.hget(RedisKeys.userInfo(reply.getFromUserId()),"userNickname"));
            reply.setToNickname(jedis.hget(RedisKeys.userInfo(reply.getToUserId()),"userNickname"));
        }
        return replyList;
    }

    /**
     * @title 发表回复
     * @description 发送通知
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    @Transactional
    public int publishReply(PublishReply request){
        int ans=replyDao.publishReply(request);
        if(ans<=0){
            return ans;
        }
        int toUserId,itemId=request.getItemId();
        if (request.getFlag()==0){
            toUserId= Integer.parseInt(jedis.hget(RedisKeys.activityInfo(itemId),"userId"));
        }else {
            toUserId= Integer.parseInt(jedis.hget(RedisKeys.dynamicInfo(itemId),"userId"));
        }
        jedis.incr(RedisKeys.noticeUnread(2,toUserId));
        if (jedis.exists(RedisKeys.online(toUserId,"msgPage"))){
            Map<String,Object> map=new HashMap<>();
            map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,toUserId)));
            map.put("likeCount",jedis.get(RedisKeys.noticeUnread(1,toUserId)));
            map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,toUserId)));
            map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,toUserId)));
            socketClient.send(String.valueOf(toUserId),"msgPage","notice",map);
        }
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
