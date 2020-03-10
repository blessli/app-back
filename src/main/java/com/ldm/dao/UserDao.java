package com.ldm.dao;

import com.ldm.entity.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    UserInfo selectUserBase(int userId);
    int register(String phone,String salt,String password);

}
