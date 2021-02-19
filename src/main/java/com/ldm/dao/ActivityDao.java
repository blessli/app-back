package com.ldm.dao;

import com.ldm.entity.ActivityIndex;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.ActivityMember;
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
     * @description 返回activityId,注意新增了share_count字段
     * @author lidongmingƒ
     * @updateTime 2020/4/4 4:29
     */
    @Insert("INSERT INTO `t_activity`(`activity_name`, `user_id`, `activity_type`, " +
            "`location_name`, `longitude`, `latitude`, `begin_time`, `end_time`, `require`," +
            " `remark`, `view_count`, `comment_count`, `member_count`, `publish_time`, `images`, " +
            "`status`) VALUES (#{activityName}, #{userId}, #{activityType}, #{locationName}," +
            " #{longitude}, #{latitude}, #{beginTime}, #{endTime}, #{require}, #{remark}," +
            " 0, 0, 0, NOW(), #{images}, 1)")
    @Options(useGeneratedKeys = true,keyProperty = "activityId",keyColumn = "activity_id")
    int publishActivity(PublishActivity request);

    @Delete("UPDATE t_activity SET status=0 where activity_id=#{activityId}")
    int deleteActivity(int activityId);

    /**
     * @title 获取最新发布的活动
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:34
     */
    @Select("SELECT * FROM t_activity where status=1 ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize};")
    List<ActivityIndex> selectActivityListByTime(int pageNum,int pageSize);

    @Select("SELECT * FROM t_activity where status=1 ORDER BY score DESC LIMIT #{pageNum},#{pageSize};")
    List<ActivityIndex> selectActivityListByHot(int pageNum,int pageSize);

    @Select("SELECT * FROM t_activity where activity_type=#{activityType} and status=1 ORDER BY score DESC LIMIT #{pageNum},#{pageSize};")
    List<ActivityIndex> selectActivityListBySort(String activityType,int pageNum,int pageSize);


    /**
     * @title 获取该用户申请加入的活动
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:36
     */
    @Select("SELECT t2.activity_id,t2.activity_name,t2.location_name,t2.begin_time," +
            "t2.end_time,t2.member_count,t2.images image,t2.publish_time,t1.status FROM " +
            "t_join t1 LEFT JOIN t_activity t2 ON t1.activity_id=t2.activity_id WHERE " +
            "t1.user_id=#{userId} ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<MyActivity> selectMyActivityList(int userId,int pageNum,int pageSize);


    /**
     * @title 获取我发布的活动列表
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:37
     */
    @Select("SELECT * FROM t_activity WHERE user_id=#{userId} and status=1 ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<ActivityIndex> selectActivityCreatedByMe(int userId,int pageNum,int pageSize);

    /**
     * @title 获取该活动的详情内容
     * @description
     * @author lidongming
     * @updateTime 2020/4/6 14:40
     */
    @Select("select * from t_activity where activity_id=#{activityId}")
    ActivityDetail selectActivityDetail(int activityId);

    /**
     * @title 用户第一次点击该活动,浏览量+1
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/6 14:40 
     */
    @Update("UPDATE t_activity SET view_count=view_count+1 WHERE activity_id=#{activityId}")
    int addViewCount(int activityId,int userId);
    /**
     * @title 用户申请加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:51
     */
    @Insert("INSERT INTO t_join VALUES(NULL,#{userId},#{toUserId},#{activityId},0,NOW())")
    int tryJoinActivity(int activityId,int userId,int toUserId);

    /**
     * @title 用户取消加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:55
     */
    @Delete("DELETE FROM t_join WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int cancelJoinActivity(int activityId,int userId);

    /**
     * @title 活动发布者同意该用户加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:55
     */
    @Update("UPDATE t_join SET `status`=2 WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int agreeJoinActivity(int activityId,int userId);

    /**
     * @title 活动发布者拒绝该加入活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 19:54
     */
    @Update("UPDATE t_join SET `status`=1 WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int disagreeJoinActivity(int activityId,int userId);

    /**
     * @title 根据es查询出来的activityId列表,在mysql中查询
     * @description 走xml
     * @author ggh
     * @updateTime 2020/4/14 17:18
     */
    List<ActivityIndex> selectActivityListByEs(List<Integer> activityIdList);

    /**
     * @title 异步更新活动分数
     * @description
     * @author lidongming
     * @updateTime 2020/4/16 14:40
     */
    @Update("update t_activity set score=#{score} where activity_id=#{activityId}")
    int updateActivityScore(int activityId,double score);

    @Select("select * from t_join where activity_id=#{activityId} and status=2")
    List<ActivityMember> getActivityMemberList(int activityId);
}
