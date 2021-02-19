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
    @PostMapping(value = "/user/info")
    public JSONResult addUserInfo(@RequestBody UserInfo userInfo) throws Exception {
        log.info("获取用户信息");
        return JSONResult.success();
    }

    @Action(name = "用户个人中心")
    @GetMapping("/user/profile")
    public JSONResult getUserProfile(int userId,int myUserId) {
        log.info("{} 获取用户 {} 的个人主页", myUserId,userId);
        return JSONResult.success(userService.getUserProfile(userId,myUserId));
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
