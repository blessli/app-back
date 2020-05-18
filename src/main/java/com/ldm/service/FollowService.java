package com.ldm.service;

import com.ldm.dao.FollowDao;
import com.ldm.netty.SocketClientComponent;
import com.ldm.pojo.FollowOrNot;
import com.ldm.rabbitmq.MQSender;
import com.ldm.entity.FollowUserInfo;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * @author lidongming
 * @ClassName FollowService.java
 * @Description 关注服务
 * @createTime 2020年04月17日 19:55:00
 */
@Service
public class FollowService {

    @Autowired
    private JedisCluster jedis;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private SocketClientComponent socketClient;

    @Autowired
    private FollowDao followDao;

    /**
     * @title 获取关注列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:12
     */
    public List<FollowUserInfo> getMeFollowUserList(int userId){
        List<FollowUserInfo> followUserInfoList=followDao.getMeFollowUserList(userId);
        int currUserId;
        for (FollowUserInfo followUserInfo:followUserInfoList){
            currUserId=followUserInfo.getUserId();
            followUserInfo.setAvatar(jedis.hget(RedisKeys.userInfo(currUserId),"avatar"));
            followUserInfo.setUserNickname(jedis.hget(RedisKeys.userInfo(currUserId),"userNickname"));
        }
        return followUserInfoList;
    }

    /**
     * @title 获取粉丝列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:15
     */
    public List<FollowUserInfo> getFollowMeUserList(int userId){
        Set<String> set=jedis.smembers(RedisKeys.followMe(userId));
        List<FollowUserInfo> list=new ArrayList<>(set.size());
        for (String string:set){
            FollowUserInfo userInfo=new FollowUserInfo();
            int currUserId=Integer.valueOf(string);
            userInfo.setUserId(currUserId);
            userInfo.setAvatar(jedis.hget(RedisKeys.userInfo(currUserId),"avatar"));
            userInfo.setUserNickname(jedis.hget(RedisKeys.userInfo(currUserId),"userNickname"));
            list.add(userInfo);
        }
        return list;
    }

    /**
     * @title 关注
     * @description 更新feed流
     * @author lidongming
     * @updateTime 2020/4/14 22:55
     */
    @Transactional
    public int followUser(int userId, int toUserId){
        jedis.sadd(RedisKeys.followMe(toUserId),userId+"");
        jedis.sadd(RedisKeys.meFollow(userId),toUserId+"");
        FollowOrNot followOrNot=new FollowOrNot();
        followOrNot.setFlag(true);
        followOrNot.setUserId(userId);
        followOrNot.setToUserId(toUserId);
        // 异步更新feed流
        mqSender.feedFollow(JsonUtil.beanToString(followOrNot));
        // 当用户A关注用户B,如果用户B在线的话就会收到这个通知
        if (jedis.exists(RedisKeys.online(toUserId,"msgPage"))){
            Map<String,Object> map=new HashMap<>();
            map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,toUserId)));
            map.put("likeCount",jedis.get(RedisKeys.noticeUnread(1,toUserId)));
            map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,toUserId)));
            map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,toUserId)));
            socketClient.send(String.valueOf(toUserId),"msgPage","notice",map);
        }
        return followDao.follow(userId, toUserId);
    }

    /**
     * @title 取消关注
     * @description 更新feed流
     * @author lidongming
     * @updateTime 2020/4/14 22:56
     */
    @Transactional
    public int cancelFollowUser(int userId,int toUserId){
        jedis.srem(RedisKeys.followMe(toUserId),userId+"");
        jedis.srem(RedisKeys.meFollow(userId),toUserId+"");
        FollowOrNot followOrNot=new FollowOrNot();
        followOrNot.setFlag(false);
        followOrNot.setUserId(userId);
        followOrNot.setToUserId(toUserId);
        // 异步更新feed流
        mqSender.feedFollow(JsonUtil.beanToString(followOrNot));
        return followDao.cancelFollow(userId, toUserId);
    }


    /**
     * @title 关注
     * @description 更新feed流
     * @author lidongming
     * @updateTime 2020/4/14 22:55
     */
    public void followUserSyncRedis(int userId, int toUserId){
        jedis.sadd(RedisKeys.followMe(toUserId),userId+"");
        jedis.sadd(RedisKeys.meFollow(userId),toUserId+"");
        FollowOrNot followOrNot=new FollowOrNot();
        followOrNot.setFlag(true);
        followOrNot.setUserId(userId);
        followOrNot.setToUserId(toUserId);
        // 异步更新feed流
        mqSender.feedFollow(JsonUtil.beanToString(followOrNot));
    }
}
