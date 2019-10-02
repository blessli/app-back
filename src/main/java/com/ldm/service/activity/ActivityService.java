package com.ldm.service.activity;

import com.ldm.entity.activity.*;
import com.ldm.request.PublishActivityRequest;

import java.util.List;

public interface ActivityService {
    void publish(PublishActivityRequest publishActivityRequest);
    List<Activity> selectActivityList();
    ActivityDetail selectActivityDetail(int activityId);
    List<ActivityReply> selectActivityReplyList(int commentId);
    void clickActivityDetail(int activityId);
}
