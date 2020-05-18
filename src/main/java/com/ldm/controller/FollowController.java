package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.service.FollowService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * @author lidongming
 * @ClassName FollowController.java
 * @Description 关注服务
 * @createTime 2020年04月17日 19:45:00
 */
@Slf4j
@RestController
@Validated
public class FollowController {
    
    @Autowired
    private FollowService followService;

    @Action(name = "获取关注列表")
    @GetMapping("/follow/meFollow")
    public JSONResult getMeFollowUserList(@Valid @Min(1) int userId){
        log.info("获取用户 {} 的关注列表", userId);
        return JSONResult.success(followService.getMeFollowUserList(userId));
    }

    @Action(name = "获取粉丝列表")
    @GetMapping("/follow/followMe")
    public JSONResult getFollowMeUserList(@Valid @Min(1) int userId){
        log.info("获取用户 {} 的粉丝列表",userId);
        return JSONResult.success(followService.getFollowMeUserList(userId));
    }

    @Action(name = "关注用户")
    @PostMapping(value = "/follow")
    public JSONResult followUser(@Valid @RequestParam @Min(1) int userId,@Valid @RequestParam @Min(1) int toUserId){
        log.info("用户 {} 关注用户 {}", userId, toUserId);
        return JSONResult.success(followService.followUser(userId, toUserId));
    }

    @Action(name = "取消关注用户")
    @PostMapping(value = "/cancelFollow")
    public JSONResult cancelFollowUser(@Valid @RequestParam @Min(1) int userId,@Valid @RequestParam @Min(1) int toUserId){
        log.info("用户 {} 取消关注用户 {}", userId, toUserId);
        return JSONResult.success(followService.cancelFollowUser(userId, toUserId));
    }
}
