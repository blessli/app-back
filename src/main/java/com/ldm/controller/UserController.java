package com.ldm.controller;
import com.ldm.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户相关接口",description = "提供用户相关的REST API")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

}
