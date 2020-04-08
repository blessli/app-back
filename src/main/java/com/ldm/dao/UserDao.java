package com.ldm.dao;

import com.ldm.request.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface UserDao {
    /**
     * @title 获取该用户关注的用户列表
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/6 19:51 
     */
    @Select("SELECT user_id FROM `t_follow` WHERE follower_id=#{userId}")
    List<Integer> getFollowedUserList(int userId);

    int addUserInfo(UserInfo userInfo);
    int isFirstLogin(String openId);
}
