package com.ldm.dao;

import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamic;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface DynamicDao {
    /**
     * @title 发表动态
     * @description 发表动态
     * @author lidongming
     * @updateTime 2020/4/4 4:36
     */
    @Insert("INSERT INTO `t_dynamic`(`content`, `images`, `publish_location`, `user_id`, " +
            "`comment_count`, `like_count`, `dynamic_score`, `publish_time`) " +
            "VALUES (#{content}, #{images}, #{publishLocation}, #{userId}, " +
            "0, 0, 0, #{publishTime})")
    int publishDynamic(PublishDynamic request);

    /**
     * @title 获取已关注者发表的动态
     * @description 最新发表
     * @author lidongming
     * @updateTime 2020/4/7 1:31
     */
    @Select("SELECT t_dynamic.*,t_user.avatar,t_user.user_nickname,IFNULL(t_dynamic_like.id,-1) AS is_like FROM `t_follow` INNER JOIN t_dynamic\n" +
            "ON t_follow.follower_id=#{userId} AND t_dynamic.user_id=t_follow.user_id\n" +
            "LEFT JOIN t_user ON t_user.user_id=t_follow.user_id\n" +
            "LEFT JOIN t_dynamic_like ON t_dynamic.dynamic_id=t_dynamic_like.dynamic_id AND #{userId}=t_dynamic_like.user_id\n" +
            "ORDER BY publish_time DESC")
    List<Dynamic> selectDynamicList(int userId);
    /**
     * @title 删除动态
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/6 15:37 
     */
    @Delete("delete from t_dynamic where dynamic_id=#{dynamicId}")
    int deleteDynamic(int dynamicId);

    /**
     * @title 点赞动态
     * @description 给动态点赞
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    @Update("")
    int likeDynamic(int dynamicId,int userId);
    /**
     * @title 取消点赞动态
     * @description 取消给动态点赞
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    @Update("")
    int cancellikeDynamic(int dynamicId,int userId);
}
