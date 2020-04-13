package com.ldm.dao;

import com.ldm.entity.Activity;
import com.ldm.entity.ActivityApply;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.MyActivity;
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
            " 0, 0, 0, NOW(), #{images}, 0)")
    @Options(useGeneratedKeys = true,keyProperty = "activityId",keyColumn = "activity_id")
    int publishActivity(PublishActivity request);

    /**
     * @title 删除活动
     * @description 将所有与活动相关的都删除
     * @author lidongming
     * @updateTime 2020/4/4 4:30
     */
    @Delete("DELETE from t_reply where comment_id in (SELECT t_comment.comment_id from t_comment WHERE item_id=#{activityId} AND flag=0);\n" +
            "DELETE FROM t_comment WHERE item_id=#{activityId} AND flag=0;\n" +
            "DELETE FROM t_activity_member WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_activity_join_request WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_activity_view WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_activity WHERE activity_id=#{activityId};")
    int deleteActivity(int activityId);

    /**
     * @title 获取最新发布的活动
     * @description 获取最新发布的活动,暂时不考虑分页
     * @author lidongming
     * @updateTime 2020/4/6 14:40
     */
    @Select("SELECT t_activity.*,t_user.avatar,t_user.user_nickname FROM t_activity\n" +
            "LEFT JOIN t_user ON t_activity.user_id=t_user.user_id ORDER BY publish_time DESC")
    List<Activity> selectActivityListByTime(int pageNum,int pageSize);


    /**
     * @title 获取该用户申请加入的活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:57
     */
    @Select("SELECT t.activity_id,t.`status`,tt.activity_name,tt.begin_time,tt.end_time,tt.images as image,tt.location_name\n" +
            ",tt.member_count,tt.publish_time FROM t_activity_join_request t\n" +
            "LEFT JOIN t_activity tt ON tt.activity_id=t.activity_id\n" +
            "WHERE t.user_id=#{userId} ORDER BY t.publish_time DESC")
    List<MyActivity> selectMyActivityList(int userId,int pageNum,int pageSize);

    /**
     * @title 获取我发布的活动列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 20:57
     */
    @Select("SELECT t_activity.*,avatar,user_nickname FROM t_activity\n" +
            "LEFT JOIN t_user ON t_user.user_id=t_activity.user_id\n" +
            "WHERE t_activity.user_id=#{userId} ORDER BY publish_time DESC")
    List<Activity> selectActivityCreatedByMe(int userId,int pageNum,int pageSize);

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
     * @title 用户第一次点击该活动,浏览量+1
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/6 14:40 
     */
    @Insert("INSERT INTO t_activity_view VALUES(NULL,#{activityId},#{userId});" +
            "UPDATE t_activity SET view_count=view_count+1 WHERE activity_id=#{activityId}")
    int addViewCount(int activityId,int userId);

    /**
     * @title 检查该用户是否第一次点击该活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/9 17:55
     */
    @Select("SELECT COUNT(id) FROM t_activity_view WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int isFirstClickActivity(int activityId,int userId);
    /**
     * @title 用户申请加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:51
     */
    @Insert("INSERT INTO t_activity_join_request VALUES(NULL,#{userId},#{activityId},0,NOW())")
    int tryJoinActivity(int activityId,int userId);

    /**
     * @title 用户取消加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:55
     */
    @Delete("DELETE FROM t_activity_join_request WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int cancelJoinActivity(int activityId,int userId);

    /**
     * @title 活动发布者同意该用户加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:55
     */
    @Update("UPDATE t_activity_join_request SET `status`=2 WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int agreeJoinActivity(int activityId,int userId);

    /**
     * @title 活动发布者拒绝该加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:54
     */
    @Update("UPDATE t_activity_join_request SET `status`=1 WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int disagreeJoinActivity(int activityId,int userId);

    /**
     * @title 获取该用户发表的活动接收到的申请通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/12 0:34
     */
    @Select("SELECT t.user_id,t.activity_id,t.publish_time,t.`status`,avatar,user_nickname,tt.activity_name FROM t_activity_join_request t\n" +
            "LEFT JOIN t_user ON t_user.user_id=t.user_id\n" +
            "INNER JOIN (SELECT t_activity.activity_id,activity_name \n" +
            "FROM t_activity WHERE t_activity.user_id=#{userId}) tt ON t.activity_id=tt.activity_id")
    List<ActivityApply> selectActivityApplyList(int userId,int pageNum,int pageSize);

}
