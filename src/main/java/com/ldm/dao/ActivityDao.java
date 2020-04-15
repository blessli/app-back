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
     * @description 返回activityId
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
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:34
     */
    @Select("SELECT * FROM t_activity ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize};")
    List<Activity> selectActivityListByTime(int pageNum,int pageSize);



    /**
     * @title 获取该用户申请加入的活动
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:36
     */
    @Select("SELECT\n" +
            "t2.activity_id,\n" +
            "t2.activity_name,\n" +
            "t2.location_name,\n" +
            "t2.begin_time,\n" +
            "t2.end_time,\n" +
            "t2.member_count,\n" +
            "t2.images image,\n" +
            "t2.publish_time,\n" +
            "t1.status\n" +
            "FROM \n" +
            "(\n" +
            "SELECT\n" +
            "user_id,\n" +
            "activity_id,\n" +
            "status \n" +
            "FROM t_activity_join_request \n" +
            "WHERE user_id=#{userId}\n" +
            ") t1\n" +
            "LEFT JOIN t_activity t2 \n" +
            "ON t1.activity_id=t2.activity_id LIMIT #{pageNum}, #{pageSize}")
    List<MyActivity> selectMyActivityList(int userId,int pageNum,int pageSize);


    /**
     * @title 获取我发布的活动列表
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:37
     */
    @Select("SELECT * FROM t_activity WHERE user_id=#{userId} LIMIT #{pageNum},#{pageSize}")
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
    ActivityDetail selectActivityDetail(int activityId);

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
     * @author ggh
     * @updateTime 2020/4/14 19:38
     */
    @Select("SELECT * FROM\n" +
            "(" +
            "SELECT t2.user_id,t2.publish_time,t1.activity_name,t2.status FROM " +
            "(SELECT activity_id,activity_name FROM t_activity WHERE user_id=#{userId}) t1" +
            "JOIN t_activity_join_request t2 ON t1.activity_id=t2.activity_id ORDER BY publish_time DESC" +
            ") t3 " +
            " LIMIT #{pageNum},#{pageSize}")
    List<ActivityApply> selectActivityApplyList(int userId,int pageNum,int pageSize);

    /**
     * @title 根据es查询出来的activityId列表,在mysql中查询
     * @description
     * @author ggh
     * @updateTime 2020/4/14 17:18
     */
    List<Activity> selectActivityListByEs(List<Integer> activityIdList);
}
