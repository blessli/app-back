package com.ldm.dao;

import com.ldm.entity.SimpleUserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FollowDao {
    /**
     * @title 获取关注列表
     * @description
     * @author ggh
     * @updateTime 2020/4/6 19:51
     */
    List<SimpleUserInfo> getMeFollowUserList(int userId, int pageNum,int pageSize);

    /**
     * @title 获取粉丝列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/14 19:42
     */
    @Select("SELECT follower_id user_id,publish_time FROM t_follow WHERE user_id=#{userId}" +
            "LIMIT #{pageNum},#{pageSize}")
    List<SimpleUserInfo> getFollowMeUserList(int userId, int pageNum,int pageSize);

    /**
     * @title 关注
     * @description
     * @author ggh
     * @updateTime 2020/4/17 20:30
     */
    @Insert("")
    int follow(int userId,int toUserId);

    /**
     * @title 取消关注
     * @description
     * @author ggh
     * @updateTime 2020/4/17 20:30
     */
    @Insert("")
    int cancelFollow(int userId,int toUserId);
}
