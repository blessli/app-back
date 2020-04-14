package com.ldm.dao;

import com.ldm.entity.SimpleUserInfo;
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
    @Select("SELECT t_follow.user_id,user_nickname,avatar FROM t_follow \n" +
            "LEFT JOIN t_user ON t_user.user_id=t_follow.user_id WHERE follower_id=#{userId}")
    List<SimpleUserInfo> getFollowedUserList(int userId);

    /**
     * @title 获取关注该用户的用户列表
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:42
     */
    @Select("SELECT t_follow.follower_id as user_id,user_nickname,avatar FROM t_follow \n" +
            "LEFT JOIN t_user ON t_user.user_id=t_follow.follower_id WHERE t_follow.user_id=#{userId} LIMIT #{pageNum}, #{pageSize}")
    List<SimpleUserInfo> getFollowMeUserList(int userId, int pageNum,int pageSize);

    int addUserInfo(UserInfo userInfo);

    int isFirstLogin(String openId);

    @Select("SELECT user_id,user_nickname,avatar FROM `t_user`")
    List<SimpleUserInfo> selectSimpleUserInfo();
}
