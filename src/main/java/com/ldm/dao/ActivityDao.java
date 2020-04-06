package com.ldm.dao;

import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.request.PublishActivity;
import org.apache.ibatis.annotations.*;

import java.util.List;
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
    @Delete("delete from t_activity where activityId=#{activityId}")
    int deleteActivity(int activityId);

    /**
     * @title 更新活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 4:30
     */
    int updateActivity(int activityId);

    /**
     * 获取最新发布的活动
     * @return
     */
    @Select("SELECT * FROM `t_activity` ORDER BY create_time limit 0,10")
    List<Activity> selectActivityListByTime();

    /**
     * 根据活动类型列表进行筛选活动-sql写在xml里
     * @param activityTypeList
     * @return
     */
    List<Activity> selectActivityListByActivityType(List<String> activityTypeList);

    /**
     * 根据用户关注的用户列表进行筛选活动
     * @param userId
     * @return
     */
    @Select("SELECT * FROM t_activity WHERE user_id IN (SELECT following_id FROM t_follow WHERE follower_id=#{userId})")
    List<Activity> selectActivityListByFollowedUserList(int userId);

    /**
     * @title 筛选活动
     * @description 根据活动的热度进行排序，基于hacker news算法
     * @author lidongming
     * @updateTime 2020/4/4 4:31
     */

    List<Activity> selectActivityListByHot();

    /**
     * 获取该活动的详情内容
     * @param activityId
     * @return
     */
    ActivityDetail selectActivityDetail(int activityId,int userId);

    /**
     * 用户首次进入活动详情页，浏览量+1
     * @param activityId
     */
    void clickActivityDetail(int activityId,int userId);

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
    int agreeJoinActiviy(int activityId,int userId);

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=1,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int disagreeJoinActiviy(int activityId,int userId);

    /**
     * 活动发布者将某用户从该活动中删除
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=3,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int deleteJoinedActivity(int activityId,int userId);

    /**
     * 活动发布者邀请该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Insert("INSERT INTO `t_activity_join_request`(`user_id`, `activity_id`, `status`, `publish_time`) " +
            "VALUES (#{userId}, #{activityId}, 0, NOW())")
    int inviteJoinActivity(int activityId,int userId);

}
