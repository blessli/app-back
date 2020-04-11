package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.request.UserInfo;
import com.ldm.service.UserService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
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

    /**
     * @title 获取该用户关注的用户列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:12
     */
    @Action(name = "获取该用户关注的用户列表")
    @GetMapping("/user/followed")
    public JSONResult getFollowedUserList(int userId){
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
    public JSONResult getFollowMeUserList(int userId){
        return JSONResult.success(userService.getFollowMeUserList(userId));
    }
}
