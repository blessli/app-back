package com.ldm.service.activity.impl;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.entity.ActivityReply;
import com.ldm.entity.UserInfo;
import com.ldm.request.PublishActivityRequest;
import com.ldm.service.activity.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "/activityService")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;

    @Override
    public boolean publish(PublishActivityRequest publishActivityRequest) {
        return false;
    }

    @Override
    public boolean deleteActivity(int activityId) {
        return false;
    }

    @Override
    public boolean updateActivity(int activityId) {
        return false;
    }

    @Override
    public List<Activity> selectActivityListByTime() {
        return null;
    }

    @Override
    public List<Activity> selectActivityListByActivityType(List<String> activityTypeList) {
        return null;
    }

    @Override
    public List<Activity> selectActivityListByFollowedUserList(List<Integer> followedUserList) {
        return null;
    }

    @Override
    public List<Activity> selectActivityListByHot(int userId) {
        return null;
    }

    @Override
    public ActivityDetail selectActivityDetail(int activityId) {
        return null;
    }

    @Override
    public List<ActivityReply> selectActivityReplyList(int commentId) {
        return null;
    }

    @Override
    public List<UserInfo> selectJoinedUserList(int activityId) {
        return null;
    }

    @Override
    public boolean publishComment(int activityId, int userId) {
        return false;
    }

    @Override
    public boolean publishReply(int commentId, int fromUserId, int toUserId) {
        return false;
    }

    @Override
    public void clickActivityDetail(int activityId, int userId) {

    }

    @Override
    public boolean tryJoinActivity(int activityId, int userId) {
        return false;
    }

    @Override
    public boolean cancelJoinActivity(int activityId, int userId) {
        return false;
    }

    @Override
    public boolean agreeJoinActiviy(int activityId, int userId) {
        return false;
    }

    @Override
    public boolean disagreeJoinActiviy(int activityId, int userId) {
        return false;
    }

    @Override
    public boolean deleteJoinedActivity(int activityId, int userId) {
        return false;
    }

    @Override
    public boolean inviteJoinActivity(int activityId, int userId) {
        return false;
    }
}
