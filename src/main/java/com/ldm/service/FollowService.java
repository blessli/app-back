package com.ldm.service;

import com.ldm.dao.FollowDao;
import com.ldm.pojo.FollowOrNot;
import com.ldm.rabbitmq.MQSender;
import com.ldm.response.FollowUserInfo;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lidongming
 * @ClassName FollowService.java
 * @Description 关注服务
 * @createTime 2020年04月17日 19:55:00
 */
@Service
public class FollowService {

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private FollowDao followDao;

    /**
     * @title 获取关注列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:12
     */
    public List<FollowUserInfo> getMeFollowUserList(int userId){
        Jedis jedis=jedisPool.getResource();
        Set<String> set=jedis.smembers(RedisKeys.meFollow(userId));
        List<FollowUserInfo> list=new ArrayList<>(set.size());
        int currUserId;
        for (String string:set){
            FollowUserInfo userInfo=new FollowUserInfo();
            currUserId=Integer.valueOf(string);
            userInfo.setUserId(currUserId);
            userInfo.setAvatar(jedis.hget(RedisKeys.userInfo(currUserId),"avatar"));
            userInfo.setUserNickname(jedis.hget(RedisKeys.userInfo(currUserId),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return list;
    }

    /**
     * @title 获取粉丝列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:15
     */
    public List<FollowUserInfo> getFollowMeUserList(int userId){
        Jedis jedis=jedisPool.getResource();
        Set<String> set=jedis.smembers(RedisKeys.followMe(userId));
        List<FollowUserInfo> list=new ArrayList<>(set.size());
        for (String string:set){
            FollowUserInfo userInfo=new FollowUserInfo();
            int currUserId=Integer.valueOf(string);
            userInfo.setUserId(currUserId);
            userInfo.setAvatar(jedis.hget(RedisKeys.userInfo(currUserId),"avatar"));
            userInfo.setUserNickname(jedis.hget(RedisKeys.userInfo(currUserId),"userNickname"));
        }
        CacheService.returnToPool(jedis);
        return list;
    }

    /**
     * @title 关注
     * @description 更新feed流
     * @author lidongming
     * @updateTime 2020/4/14 22:55
     */
    public int followUser(int userId, int toUserId){
        Jedis jedis=jedisPool.getResource();
        jedis.sadd(RedisKeys.followMe(toUserId),userId+"");
        jedis.sadd(RedisKeys.meFollow(userId),toUserId+"");
        //添加数据时应该先向数据库添加，同时删除redis相应的缓存数据，保持一致性，下次查询redis时没有
        //相应数据就会从数据库查，记得将查到的结果存入redis
        //操作数据库
        FollowOrNot followOrNot=new FollowOrNot();
        followOrNot.setFlag(true);
        followOrNot.setUserId(userId);
        followOrNot.setToUserId(toUserId);
        mqSender.feedFollow(JsonUtil.beanToString(followOrNot));
        CacheService.returnToPool(jedis);
        return followDao.follow(userId, toUserId);
    }

    /**
     * @title 取消关注
     * @description 更新feed流
     * @author lidongming
     * @updateTime 2020/4/14 22:56
     */
    public int cancelFollowUser(int userId,int toUserId){
        Jedis jedis=jedisPool.getResource();
        jedis.srem(RedisKeys.followMe(toUserId),userId+"");
        jedis.srem(RedisKeys.meFollow(userId),toUserId+"");
        FollowOrNot followOrNot=new FollowOrNot();
        followOrNot.setFlag(false);
        followOrNot.setUserId(userId);
        followOrNot.setToUserId(toUserId);
        mqSender.feedFollow(JsonUtil.beanToString(followOrNot));
        CacheService.returnToPool(jedis);
        return followDao.cancelFollow(userId, toUserId);
    }
}
