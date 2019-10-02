package com.ldm.service.activity.impl;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.activity.*;
import com.ldm.request.PublishActivityRequest;
import com.ldm.service.activity.ActivityService;
import com.ldm.util.DateHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service(value = "/activityService")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Override
    public void publish(PublishActivityRequest publishActivityRequest) {

        publishActivityRequest.setPublishTime(DateHandle.currentDate());
        activityDao.publish(publishActivityRequest);
    }

    @Override
    public List<Activity> selectActivityList() {
        List<Activity> activityList=activityDao.selectActivityList();
        for (Activity activity: activityList){
            activity.setImageList(Arrays.asList(activity.getImages().split(",")));
        }
        return activityList;
    }

    @Override
    public ActivityDetail selectActivityDetail(int activityId) {
        ActivityDetail activityDetail=new ActivityDetail();
        Activity activity=activityDao.selectActivity(activityId);
        activity.setImageList(Arrays.asList(activity.getImages().split(",")));
        activityDetail.setActivity(activity);
        activityDetail.setActivityMemberList(activityDao.selectActivityMemberList(activityId));
        activityDetail.setActivityCommentList(activityDao.selectActivityCommentList(activityId));
        return activityDetail;
    }

    @Override
    public List<ActivityReply> selectActivityReplyList(int commentId) {
        return activityDao.selectActivityReplyList(commentId);
    }

    @Override
    public void clickActivityDetail(int activityId) {
        activityDao.clickActivityDetail(activityId);
    }
}
