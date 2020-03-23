package com.ldm.dao;

import com.ldm.entity.activity.Activity;
import com.ldm.entity.activity.ActivityComment;
import com.ldm.entity.activity.ActivityDetail;
import com.ldm.entity.activity.ActivityReply;
import com.ldm.entity.user.UserInfo;
import com.ldm.request.PublishActivityRequest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface ActivityDao {
    /**
     * 用户发布活动
     * @param publishActivityRequest
     */
    @Insert("")
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
     * 根据活动类型列表进行筛选活动
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
    ActivityDetail selectActivityDetail(int activityId,int userId);

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
    List<ActivityReply> selectActivityReplyList(int commentId);

    /**
     * 获取成功加入活动的用户列表
     * @param activityId
     * @return
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
     */
    void clickActivityDetail(int activityId,int userId);

    /**
     * 用户申请加入活动
     * @param activityId
     * @param userId
     * @return
     */
    boolean tryJoinActivity(int activityId,int userId);

    /**
     * 用户取消加入活动
     * @param activityId
     * @param userId
     * @return
     */
    boolean cancelJoinActivity(int activityId,int userId);

    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    boolean agreeJoinActiviy(int activityId,int userId);

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    boolean disagreeJoinActiviy(int activityId,int userId);

    /**
     * 活动发布者将某用户从该活动中删除
     * @param activityId
     * @param userId
     * @return
     */
    boolean deleteJoinedActivity(int activityId,int userId);

    /**
     * 活动发布者邀请该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    boolean inviteJoinActivity(int activityId,int userId);

}
