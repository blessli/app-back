package com.ldm.dao;

import com.ldm.entity.FollowUserInfo;
import com.ldm.entity.RedisUserId;
import com.ldm.entity.SimpleUserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FollowDao {

    // 获取关注列表
    @Select("SELECT user_id FROM t_follow WHERE follower_id=#{userId} ORDER BY id DESC")
    List<FollowUserInfo> getMeFollowUserList(int userId);

    // 获取粉丝列表
    @Select("SELECT follower_id as user_id FROM t_follow WHERE user_id=#{userId}" +
            " ORDER BY id DESC")
    List<FollowUserInfo> getFollowMeUserList(int userId);

    // 关注
    @Insert("INSERT INTO t_follow VALUES(NULL,#{userId}, #{toUserId}, NOW());" +
            "UPDATE t_user set focus_count=focus_count+1 WHERE user_id=#{userId};" +
            "UPDATE t_user set fan_count=fan_count+1 WHERE user_id=#{toUserId}")
    int follow(int userId,int toUserId);

    // 取消关注
    @Insert("DELETE FROM t_follow WHERE follower_id=#{userId} AND user_id=#{toUserId};" +
            "UPDATE t_user set focus_count=focus_count-1 WHERE user_id=#{userId};" +
            "UPDATE t_user set fan_count=fan_count-1 WHERE user_id=#{toUserId}")
    int cancelFollow(int userId,int toUserId);
}