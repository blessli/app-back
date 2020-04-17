package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.service.FollowService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lidongming
 * @ClassName FollowController.java
 * @Description 关注服务
 * @createTime 2020年04月17日 19:45:00
 */
@Slf4j
@RestController
public class FollowController {
    
    @Autowired
    private FollowService followService;
    
    /**
     * @title 获取关注列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:12
     */
    @Action(name = "获取关注列表")
    @GetMapping("/follow/meFollow")
    public JSONResult getMeFollowUserList(int userId){
        log.debug("获取用户 {} 关注的用户列表", userId);
        return JSONResult.success(followService.getMeFollowUserList(userId));
    }

    /**
     * @title 获取粉丝列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:15
     */
    @Action(name = "获取粉丝列表")
    @GetMapping("/follow/followMe")
    public JSONResult getFollowMeUserList(int userId){
        log.debug("获取关注了用户 {} 的用户列表");
        return JSONResult.success(followService.getFollowMeUserList(userId));
    }

    @Action(name = "关注用户")
    @PostMapping(value = "/follow")
    public JSONResult followUser(int userId,int toUserId){
        log.debug("用户 {} 关注用户 {}", userId, toUserId);
        return JSONResult.success(followService.followUser(userId, toUserId));
    }

    @Action(name = "取消关注用户")
    @PostMapping(value = "/cancelFollow")
    public JSONResult cancelFollowUser(int userId,int toUserId){
        log.debug("用户 {} 取关用户 {}", userId, toUserId);
        return JSONResult.success(followService.cancelFollowUser(userId, toUserId));
    }
}
