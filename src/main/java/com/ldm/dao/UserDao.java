package com.ldm.dao;

import com.ldm.entity.user.UserInfo;

public interface UserDao {
    UserInfo selectUserBase(String userId);
}
