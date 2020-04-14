package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.request.UserInfo;
import com.ldm.response.UserProfile;
import com.ldm.service.CacheService;
import com.ldm.service.UserService;
import com.ldm.util.JSONResult;
import com.ldm.util.RedisKeys;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;
    @Action(name = "登录凭证校验")
    @GetMapping("/user/login")
    public JSONResult getActivityDetail(String code) throws Exception {
        return JSONResult.success(userService.loginCredentialVerification(code));
    }
    @Action(name = "获取用户信息")
    @PostMapping(value = "/user/info",consumes = "application/json")
    public JSONResult addUserInfo(@RequestBody UserInfo userInfo) throws Exception {
        log.debug("获取用户信息");
        return JSONResult.success();
    }

    @Action(name = "用户个人中心")
    @GetMapping("/user/profile")
    public JSONResult getUserProfile(int userId) {
        Jedis jedis=jedisPool.getResource();
        UserProfile userProfile=new UserProfile();
        userProfile.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
        userProfile.setUserNickname(jedis.hget(RedisKeys.userInfo(userId),"userNickname"));
        userProfile.setFanCount(jedis.zcard(RedisKeys.followMe(userId)));
        userProfile.setFocusCount(jedis.zcard(RedisKeys.meFollow(userId)));
        return JSONResult.success();
    }
    /**
     * @title 获取该用户关注的用户列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:12
     */
    @Action(name = "获取该用户关注的用户列表")
    @GetMapping("/user/followed")
    public JSONResult getFollowedUserList(int userId,int pageNum,int pageSize){
        log.debug("获取用户 {} 关注的用户列表，当前页为：{}", userId, pageNum);
        return JSONResult.success(userService.getFollowedUserList(userId, pageNum, pageSize));
    }

    /**
     * @title 获取关注该用户的用户列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:15
     */
    @Action(name = "获取关注该用户的用户列表")
    @GetMapping("/user/followMe")
    public JSONResult getFollowMeUserList(int userId,int pageNum,int pageSize){
        log.debug("获取关注了用户 {} 的用户列表，当前页为：{}", userId, pageNum);
        return JSONResult.success(userService.getFollowMeUserList(userId, pageNum, pageSize));
    }

    @Action(name = "获取关注该用户的用户列表通知")
    @GetMapping("/user/followMe/notice")
    public JSONResult getFollowMeNoticeList(int userId,int pageNum,int pageSize){
        log.debug("获取用户 {} 的关注通知", userId);
        Jedis jedis=jedisPool.getResource();
        jedis.set(RedisKeys.commentNoticeUnread(3,userId),"0");
        returnToPool(jedis);
        //应该判断从redis有没有获取到相应的数据。没有的话应该从数据库获取，获取到后再存入redis，同时设置过期时间
        return JSONResult.success(userService.getFollowMeUserList(userId, pageNum, pageSize));
    }

    @Action(name = "关注用户")
    @PostMapping(value = "/user/follow")
    public JSONResult followUser(int userId,int toUserId){
        log.debug("用户 {} 关注用户 {}", userId, toUserId);
        Jedis jedis=jedisPool.getResource();
        long now=System.currentTimeMillis();
        jedis.zadd(RedisKeys.followMe(toUserId),now,userId+"");
        jedis.zadd(RedisKeys.meFollow(userId),now,toUserId+"");
        //添加数据时应该先向数据库添加，同时删除redis相应的缓存数据，保持一致性，下次查询redis时没有
        //相应数据就会从数据库查，记得将查到的结果存入redis
        //操作数据库
        return JSONResult.success();
    }

    @Action(name = "取消关注用户")
    @PostMapping(value = "/user/cancelFollow")
    public JSONResult cancelFollowUser(int userId,int toUserId){
        log.debug("用户 {} 取关用户 {}", userId, toUserId);
        Jedis jedis=jedisPool.getResource();
        jedis.zrem(RedisKeys.followMe(toUserId),userId+"");
        jedis.zrem(RedisKeys.meFollow(userId),toUserId+"");
        
        return JSONResult.success();
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
