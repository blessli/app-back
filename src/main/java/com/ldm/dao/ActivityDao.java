package com.ldm.dao;

import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.request.PublishActivity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface ActivityDao {
    /**
     * @title 发表活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 4:29
     */
    @Insert("INSERT INTO `t_activity`(`activity_name`, `user_id`, `activity_type`, " +
            "`location_name`, `longitude`, `latitude`, `begin_time`, `end_time`, `require`," +
            " `remark`, `view_count`, `comment_count`, `member_count`, `publish_time`, `images`, " +
            "`status`) VALUES (#{activityName}, #{userId}, #{activityType}, #{locationName}," +
            " #{longitude}, #{latitude}, #{beginTime}, #{endTime}, #{require}, #{remark}," +
            " 0, 0, 0, #{publishTime}, #{images}, 0)")
    int publishActivity(PublishActivity request);

    /**
     * @title 删除活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 4:30
     */
    @Delete("delete from t_activity where activity_id=#{activityId}")
    int deleteActivity(int activityId);

    /**
     * @title 获取最新发布的活动
     * @description 获取最新发布的活动
     * @author lidongming
     * @updateTime 2020/4/6 14:40
     */
    @Select("SELECT t_activity.*,t_user.avatar,t_user.user_nickname FROM t_activity\n" +
            "LEFT JOIN t_user ON t_activity.user_id=t_user.user_id ORDER BY publish_time DESC")
    List<Activity> selectActivityListByTime();


    /**
     * @title 获取该活动的详情内容
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/6 14:40 
     */
    @Select("SELECT t_activity.*,avatar,user_nickname,IFNULL((SELECT t.`status` FROM t_activity_join_request t\n" +
            "WHERE t.activity_id=#{activityId} AND t.user_id=#{userId}),-1) AS is_joined FROM t_activity,t_user\n" +
            "WHERE t_activity.activity_id=#{activityId} AND t_user.user_id=#{userId}")
    ActivityDetail selectActivityDetail(int activityId,int userId);

    /**
     * @title 用户进入活动详情页，redis如果不存在{activityId:userId}，则浏览量+1
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/6 14:40 
     */
    @Insert("INSERT INTO t_activity_view VALUES(NULL,#{activityId},#{userId})")
    int addViewCount(int activityId,int userId);

    /**
     * 用户申请加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Insert("INSERT INTO `t_activity_join_request`(`user_id`, `activity_id`," +
            " `status`, `publish_time`) VALUES " +
            "(#{userId}, #{activityId}, '0', NOW())")
    int tryJoinActivity(int activityId,int userId);

    /**
     * 用户取消加入活动
     * @param activityId
     * @param userId
     * @return
     * 从请求表和成员表删除数据
     */
    @Delete({"DELETE FROM t_activity_join_request WHERE activity_id=#{activityId} AND user_id=#{userId}",
    "DELETE FROM t_activity_member WHERE activity_id='#{activityId}' AND user_id='#{userId}'"})
    int cancelJoinActivity(int activityId,int userId);

    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=2,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int agreeJoinActivity(int activityId,int userId);

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=1,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int disagreeJoinActivity(int activityId,int userId);

}
