package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.request.UserInfo;
import com.ldm.response.UserProfile;
import com.ldm.service.CacheService;
import com.ldm.service.UserService;
import com.ldm.util.JSONResult;
import com.ldm.util.RedisKeyUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CacheService cacheService;

    @Action(name = "登录凭证校验")
    @GetMapping("/user/login")
    public JSONResult getActivityDetail(String code) throws Exception {
        return JSONResult.success(userService.loginCredentialVerification(code));
    }

    @Action(name = "获取用户信息")
    @PostMapping(value = "/user/info",consumes = "application/json")
    public JSONResult addUserInfo(@RequestBody UserInfo userInfo) throws Exception {

        log.info("获取用户信息");
        //待实现

        return JSONResult.success();
    }

    @Action(name = "用户个人中心")
    @GetMapping("/user/profile")
    public JSONResult getUserProfile(int userId) {
        UserProfile userProfile=new UserProfile();
        userProfile.setAvatar(cacheService.hget(RedisKeyUtil.getUserInfo(userId),"avatar"));
        userProfile.setUserNickname(cacheService.hget(RedisKeyUtil.getUserInfo(userId),"userNickname"));
        userProfile.setFanCount(cacheService.zcard(RedisKeyUtil.followMe(userId)));
        userProfile.setFocusCount(cacheService.zcard(RedisKeyUtil.meFollow(userId)));

        //应该判断从redis有没有获取到相应的数据。没有的话应该从数据库获取，获取到后再存入redis，同时设置过期时间

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

        log.info("获取用户 {} 关注的用户列表，当前页为：{}", userId, pageNum);

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

        log.info("获取关注了用户 {} 的用户列表，当前页为：{}", userId, pageNum);
        return JSONResult.success(userService.getFollowMeUserList(userId, pageNum, pageSize));
    }

    @Action(name = "获取关注该用户的用户列表通知")
    @GetMapping("/user/followMe/notice")
    public JSONResult getFollowMeNoticeList(int userId,int pageNum,int pageSize){

        log.info("获取用户 {} 的关注通知", userId);
        cacheService.set(RedisKeyUtil.getCommentNoticeUnread(3,userId),0);

        //应该判断从redis有没有获取到相应的数据。没有的话应该从数据库获取，获取到后再存入redis，同时设置过期时间

        return JSONResult.success(userService.getFollowMeUserList(userId, pageNum, pageSize));
    }

    @Action(name = "关注用户")
    @PostMapping(value = "/user/follow")
    public JSONResult followUser(int userId,int toUserId){

        log.info("用户 {} 关注用户 {}", userId, toUserId);
        cacheService.zadd(RedisKeyUtil.followMe(toUserId),userId+"");
        cacheService.zadd(RedisKeyUtil.meFollow(userId),toUserId+"");

        //添加数据时应该先向数据库添加，同时删除redis相应的缓存数据，保持一致性，下次查询redis时没有
        //相应数据就会从数据库查，记得将查到的结果存入redis
        //操作数据库

        return JSONResult.success();
    }

    @Action(name = "取消关注用户")
    @PostMapping(value = "/user/cancelFollow")
    public JSONResult cancelFollowUser(int userId,int toUserId){

        log.info("用户 {} 取关用户 {}", userId, toUserId);
        cacheService.zrem(RedisKeyUtil.followMe(toUserId),userId+"");
        cacheService.zrem(RedisKeyUtil.meFollow(userId),toUserId+"");

        //操作数据库
        //和上面那个方法(followUser)同理

        return JSONResult.success();
    }

}
