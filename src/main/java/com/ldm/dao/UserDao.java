package com.ldm.dao;

import com.ldm.entity.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    UserInfo selectUserBase(int userId);
    int register(String phone,String salt,String password);
    @Insert("insert into t_user_tag values(NULL,#{userId},#{tagName})")
    int insertTag(int userId,String tagName);
}
