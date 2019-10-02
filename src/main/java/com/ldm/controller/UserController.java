package com.ldm.controller;

import com.ldm.request.EditUserInfoRequest;
import com.ldm.service.user.UserService;
import com.ldm.util.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户相关接口",description = "提供用户相关的REST API")
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "编辑个人资料")
    @PutMapping("/user/userInfo")
    public JSONResult editProfile(@RequestBody EditUserInfoRequest editUserInfoRequest) {
        return null;
    }
    @ApiOperation(value = "个人中心")
    @GetMapping("/user/center")
    public JSONResult getUserCenter(String userId){
        return JSONResult.success(userService.selectUserCenter(userId));
    }

}
