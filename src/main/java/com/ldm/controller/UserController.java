package com.ldm.controller;
import com.ldm.aop.Action;
import com.ldm.request.UserInfo;
import com.ldm.service.UserService;
import com.ldm.util.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Action(name = "用户个人中心")
    @GetMapping("/user/profile")
    public JSONResult getUserProfile(@Valid @Min(1) int userId,@Valid @Min(1) int myUserId) {
        log.info("获取用户 {} 的个人主页", userId);
        return JSONResult.success(userService.getUserProfile(userId,myUserId));
    }

    @Action(name = "登录凭证校验")
    @GetMapping("/user/auth")
    public JSONResult doUserAuth(@Valid @NotBlank String code) throws Exception {
        log.info("获取code {} 的登录凭证校验",code);
        return JSONResult.success(userService.loginCredentialVerification(code));
    }

    @Action(name = "添加用户信息")
    @PostMapping(value = "/user/info")
    public JSONResult addUserInfo(@RequestBody @Valid UserInfo userInfo) {
        log.info("添加用户 {} 的微信账号信息",userInfo.getOpenId());
        return JSONResult.success(userService.addUserInfo(userInfo));
    }

}
