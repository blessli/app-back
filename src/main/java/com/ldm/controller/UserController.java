package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.request.UserInfo;
import com.ldm.service.UserService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
