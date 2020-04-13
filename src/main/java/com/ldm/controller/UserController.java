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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return JSONResult.success(userService.getFollowedUserList(userId));
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
        return JSONResult.success(userService.getFollowMeUserList(userId));
    }

    @Action(name = "获取关注该用户的用户列表通知")
    @GetMapping("/user/followMe/notice")
    public JSONResult getFollowMeNoticeList(int userId,int pageNum,int pageSize){
        cacheService.set(RedisKeyUtil.getCommentNoticeUnread(3,userId),0);
        return JSONResult.success(userService.getFollowMeUserList(userId));
    }

    @Action(name = "关注用户")
    @PostMapping(value = "/user/follow")
    public JSONResult followUser(int userId,int toUserId){
        cacheService.zadd(RedisKeyUtil.followMe(toUserId),userId+"");
        cacheService.zadd(RedisKeyUtil.meFollow(userId),toUserId+"");
        return JSONResult.success();
    }

    @Action(name = "取消关注用户")
    @PostMapping(value = "/user/cancelFollow")
    public JSONResult cancelFollowUser(int userId,int toUserId){
        cacheService.zrem(RedisKeyUtil.followMe(toUserId),userId+"");
        cacheService.zrem(RedisKeyUtil.meFollow(userId),toUserId+"");
        return JSONResult.success();
    }

}
