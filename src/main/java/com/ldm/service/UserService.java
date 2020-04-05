package com.ldm.service;


import com.ldm.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @author lidongming
 * @ClassName UserService.java
 * @Description 用户服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class UserService{
    @Autowired
    private UserDao userDao;
}
