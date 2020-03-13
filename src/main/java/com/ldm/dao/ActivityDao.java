package com.ldm.dao;

import com.ldm.entity.activity.Activity;
import com.ldm.entity.activity.ActivityComment;
import com.ldm.entity.activity.ActivityMember;
import com.ldm.entity.activity.ActivityReply;
import com.ldm.request.PublishActivityRequest;

import java.util.List;

public interface ActivityDao {
    // 发布活动
    int publish(PublishActivityRequest publishActivityRequest);
    // 活动列表
    List<Activity> selectActivityList();
    // 活动成员
    List<ActivityMember> selectActivityMemberList(int activityId);
    // 活动详情
    Activity selectActivity(int activityId);
    // 活动评论
    List<ActivityComment> selectActivityCommentList(int activityId);
    // 活动评论回复
    List<ActivityReply> selectActivityReplyList(int commentId);
    // 浏览量递增
    void clickActivityDetail(int activityId);
}
