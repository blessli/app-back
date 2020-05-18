package com.ldm.dao;

import com.ldm.entity.*;
import com.ldm.request.PublishActivity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface ActivityDao {

    // redis初始化
    @Select("SELECT * FROM t_activity")
    List<ActivityIndex> selectAllActivity();

    // redis初始化
    @Select("SELECT * FROM t_activity_view")
    List<ActivityMember> selectAllActivityView();

    // es初始化
    @Select("SELECT activity_id,activity_name,activity_type,location_name,user_nickname FROM t_activity LEFT JOIN t_user ON t_activity.user_id=t_user.user_id")
    List<EsActivity> selectEsActivityList();

    // 用于同步redis,保证数据一致性(注意索引顺序)
    @Select("SELECT user_id FROM t_activity_view WHERE user_id=#{userId} AND activity_id=#{activityId}")
    RedisUserId isViewActivity(int userId,int activityId);

    // 用于同步redis,保证数据一致性(注意索引顺序)
    @Select("SELECT user_id FROM t_apply WHERE user_id=#{userId} AND activity_id=#{activityId}")
    RedisUserId isJoinActivity(int userId,int activityId);

    @Select("SELECT user_id FROM t_like WHERE activity_id=#{activityId}")
    RedisUserId isExistActivity(int activityId);

    @Select("SELECT publish_time,comment_count,view_count,share_count FROM t_activity WHERE activity_id=#{activityId}")
    ScoreParameter selectScoreParameter(int activityId);

    // 获取活动列表-按时间排序
    @Select("SELECT * FROM t_activity ORDER BY activity_id DESC LIMIT #{pageNum},#{pageSize}")
    List<ActivityIndex> selectActivityListByTime(int pageNum,int pageSize);

    // 获取活动列表-按距离排序
    @Select("select * from t_activity order by ((ACOS(SIN((#{longitude} * 3.1415) / 180 ) *SIN((latitude* 3.1415) / 180 ) +\n" +
            "COS((#{longitude}  * 3.1415) / 180 ) * COS((latitude* 3.1415) / 180 ) *COS((#{latitude} * 3.1415) / 180 \n" +
            "- (longitude* 3.1415) / 180 ) ) * 6380)) asc,activity_id DESC limit #{pageNum},#{pageSize}")
    List<ActivityIndex> selectActivityListByDistance(double longitude,double latitude,int pageNum, int pageSize);

    // 获取活动列表-按热度排序
    @Select("SELECT * FROM t_activity ORDER BY view_count DESC,activity_id DESC LIMIT #{pageNum},#{pageSize}")
    List<ActivityIndex> selectActivityListByHot(int pageNum, int pageSize);

    @Select("SELECT * FROM `t_activity` WHERE activity_type=#{activityType} ORDER BY activity_id DESC LIMIT #{pageNum},#{pageSize}")
    List<ActivitySort> selectActivityListBySort(String activityType,int pageNum, int pageSize);

    // 获取该用户申请加入的活动
    @Select("SELECT t_apply.activity_id,apply_status,t_apply.publish_time,images,begin_time,end_time,activity_name,location_name,member_count\n" +
            " FROM t_apply LEFT JOIN t_activity ON t_apply.activity_id=t_activity.activity_id WHERE t_apply.user_id=#{userId}\n" +
            "ORDER BY t_apply.id DESC LIMIT #{pageNum},#{pageSize}")
    List<MyActivity> selectActivityApplyList(int userId,int pageNum,int pageSize);


    // 获取我发布的活动列表
    @Select("SELECT * FROM t_activity WHERE user_id=#{userId} ORDER BY activity_id DESC LIMIT #{pageNum},#{pageSize}")
    List<ActivityIndex> selectActivityCreatedByMe(int userId,int pageNum,int pageSize);

    // 获取该活动的详情内容
    @Select("select * from t_activity where activity_id=#{activityId}")
    ActivityDetail selectActivityDetail(int activityId);

    // 获取活动成员列表
    @Select("SELECT * FROM `t_activity_member` WHERE activity_id=#{activityId}")
    List<ActivityMember> selectActivityMemberList(int activityId);

    // 发表活动,返回activityId,注意新增了share_count字段,这里需要更新
    @Insert("INSERT INTO t_activity VALUES(NULL,#{activityName},#{userId},#{activityType},#{locationName},#{longitude},#{latitude},#{beginTime},#{endTime},#{require},#{remark},0,0,0,0,0,NOW(),#{images},0)")
    @Options(useGeneratedKeys = true,keyProperty = "activityId",keyColumn = "activity_id")
    int publishActivity(PublishActivity request);

    // 将所有与活动相关的都删除
    @Delete("DELETE FROM t_activity WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_apply WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_activity_member WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_activity_view WHERE activity_id=#{activityId};\n" +
            "DELETE FROM t_comment WHERE item_id=#{activityId} AND flag=#{flag};\n" +
            "DELETE FROM t_reply WHERE item_id=#{activityId} AND flag=#{flag}; ")
    int deleteActivity(int activityId);

    // 用户第一次点击该活动,在t_activity_view表中记录
    @Insert("INSERT INTO t_activity_view VALUES(NULL,#{activityId},#{userId})")
    int firstClickActivity(int activityId,int userId);

    // 用户第一次点击该活动,该活动的浏览量+1
    @Update("UPDATE t_activity SET view_count=view_count+1 WHERE activity_id=#{activityId}")
    int addViewCount(int activityId);

    // 申请加入活动
    @Insert("INSERT INTO t_apply VALUES (NULL,#{userId},#{toUserId}, #{activityId}, 0, NOW())")
    int joinActivity(int activityId,int userId,int toUserId);

    // 取消加入活动
    @Delete("DELETE FROM t_apply WHERE activity_id=#{activityId} AND user_id=#{userId}")
    int cancelJoinActivity(int activityId,int userId);

    // 退出活动
    @Delete("DELETE FROM t_activity_member WHERE activity_id=#{activityId} AND user_id=#{userId};"+
            "DELETE FROM t_apply WHERE activity_id=#{activityId} AND user_id=#{userId};" +
            "UPDATE t_activity SET member_count=member_count-1 WHERE activity_id=#{activityId}")
    int exitActivity(int activityId,int userId);

    // 同意加入活动
    @Insert("INSERT INTO t_activity_member VALUES(NULL,#{userId}, #{activityId}, NOW());" +
            "UPDATE t_apply SET apply_status = 2 WHERE activity_id = #{activityId} AND user_id=#{userId};" +
            "UPDATE t_activity SET member_count=member_count+1 WHERE activity_id=#{activityId}")
    int agreeJoinActivity(int activityId,int userId);

    // 拒绝加入活动
    @Update("UPDATE t_apply SET apply_status = 1 WHERE activity_id = #{activityId} AND user_id=#{userId}")
    int disagreeJoinActivity(int activityId,int userId);

    // 异步更新活动分数
    @Update("UPDATE t_activity SET score=#{score} WHERE activity_id=#{activityId}")
    int updateActivityScore(int activityId,double score);

    // 分享活动
    @Update("UPDATE t_activity SET share_count=share_count+1 WHERE activity_id=#{activityId}")
    int shareActivity(int activityId);


    // 根据es查询出来的activityId列表,在mysql中查询(代码在xml里)
    List<ActivityIndex> selectActivityListByEs(@Param("idList") List<Integer> activityIdList);
}