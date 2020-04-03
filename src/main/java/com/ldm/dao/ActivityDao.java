package com.ldm.dao;

import com.ldm.entity.Activity;
import com.ldm.entity.ActivityComment;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.ActivityReply;
import com.ldm.entity.UserInfo;
import com.ldm.request.PublishActivityRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
public interface ActivityDao {
    /**
     * 用户发布活动
     * @param publishActivityRequest
     */
    @Insert("INSERT INTO `t_activity`(`activity_name`, `user_id`, " +
            "`activity_type`, `location_name`, `longitude`, `latitude`," +
            " `begin_time`, `end_time`, `gender_limit`, `total_count`, `remark`," +
            " `activity_view_count`, `activity_comment_count`, `activity_member_count`, " +
            "`create_time`, `update_time`, `images`) VALUES (#{activityName},#{userId}," +
            "#{activityType},#{locationName},#{longitude},#{latitude},#{beginTime}," +
            "#{endTime},#{genderLimit},#{totalCount},#{remark},0,0,0,#{publishTime}," +
            "#{publishTime},#{images})")
    boolean publish(PublishActivityRequest publishActivityRequest);

    /**
     * 用户删除活动
     * @param activityId
     * @return
     */
    @Delete("delete from t_activity where activityId=#{activityId}")
    boolean deleteActivity(int activityId);

    /**
     * 用户更新活动
     * @param activityId
     * @return
     */
    boolean updateActivity(int activityId);

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
     * 根据活动的热度进行排序，基于hacker news算法
     * @param userId
     * @return
     */
    List<Activity> selectActivityListByHot(int userId);

    /**
     * 获取该活动的详情内容
     * @param activityId
     * @return
     */
//    ActivityDetail selectActivityDetail(int activityId,int userId);
    @Select("SELECT * FROM `t_activity` WHERE activity_id = #{activityId}")
    ActivityDetail selectActivityDetail(int activityId);

    /**
     * 获取该活动的评论列表，并判断当前用户是否点赞，未点赞返回-1
     * @param activityId
     * @param userId
     * @return
     */
    @Select("SELECT t_activity_comment.*,IFNULL(t_activity_comment_like.user_id,-1) FROM t_activity_comment \n" +
            "LEFT JOIN t_activity_comment_like ON t_activity_comment.activity_id=#{activityId} AND\n" +
            "t_activity_comment.comment_id=t_activity_comment_like.comment_id AND t_activity_comment.user_id=#{userId} GROUP BY t_activity_comment.comment_id\n" +
            "LIMIT 0,10")
    List<ActivityComment> selectActivityCommentList(int activityId, int userId);

    /**
     * 获取该评论的回复列表
     * @param commentId
     * @return
     */
    @Select("SELECT t2.*, t1.avatar FROM t_user t1 RIGHT JOIN (SELECT * FROM " +
            "t_activity_reply WHERE comment_id=1) t2 ON t1.user_id = t2.from_user_id;")
    List<ActivityReply> selectActivityReplyList(int commentId);

    /**
     * 获取成功加入活动的用户列表
     * @param activityId
     * @return 在xml实现了
     */
    List<UserInfo> selectJoinedUserList(int activityId);

    /**
     * 发布评论
     * @param activityId
     * @param userId
     * @return
     */
    boolean publishComment(int activityId,int userId);

    /**
     * 在某条评论下回复
     * @param commentId
     * @param fromUserId
     * @param toUserId
     * @return
     */
    boolean publishReply(int commentId,int fromUserId,int toUserId);

    /**
     * 用户首次进入活动详情页，浏览量+1
     * @param activityId
     * 更新t_activity表和t_scan_history表
     * 待改进
     */
    @Update({"UPDATE t_activity SET activity_view_count=activity_view_count+1 WHERE activity_id=#{activityId}"
    })
    void clickActivityDetail(int activityId,int userId);

    /**
     * 用户申请加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Insert("INSERT INTO t_activity_join_request(`user_id`, `activity_id`, " +
            "`create_time`, `status`, `update_time`) VALUES(#{userId},#{activityId},NOW(),1,NOW());")
    boolean tryJoinActivity(int activityId,int userId);

    /**
     * 用户取消加入活动
     * @param activityId
     * @param userId
     * @return
     *删除t_activity_join_request表的请求记录和t_activity_member成员记录
     */
    @Delete({"DELETE FROM t_activity_join_request WHERE user_id=1 AND activity_id=122",
    "DELETE FROM t_activity_member WHERE user_id='1' AND activity_id='122'"})
    boolean cancelJoinActivity(int activityId,int userId);

    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=2,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    boolean agreeJoinActiviy(int activityId,int userId);

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=1,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    boolean disagreeJoinActiviy(int activityId,int userId);

    /**
     * 活动发布者将某用户从该活动中删除
     * @param activityId
     * @param userId
     * @return
     */
    @Update("UPDATE t_activity_join_request SET `status`=3,update_time=NOW() WHERE activity_id=#{activityId} AND user_id=#{userId}")
    boolean deleteJoinedActivity(int activityId,int userId);

    /**
     * 活动发布者邀请该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    boolean inviteJoinActivity(int activityId,int userId);

}
