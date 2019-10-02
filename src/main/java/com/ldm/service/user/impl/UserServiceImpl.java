package com.ldm.service.user.impl;

import com.ldm.dao.UserDao;
import com.ldm.entity.user.UserInfo;
import com.ldm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "/userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public UserInfo selectUserCenter(String userId) {
        return userDao.selectUserBase(userId);
    }
}
