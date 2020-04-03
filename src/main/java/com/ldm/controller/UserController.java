package com.ldm.controller;
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
    @ApiOperation(value = "个人中心")
    @GetMapping("/user/login")
    public JSONResult login(){
        return JSONResult.success();
    }
    @ApiOperation(value = "个人中心")
    @GetMapping("/user/center")
    public JSONResult getUserCenter(String userId){
        return JSONResult.success("登录成功");
    }

}
