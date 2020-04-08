package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.request.PublishActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
/**
 * @author lidongming
 * @ClassName ActivityService.java
 * @Description 活动服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private CommentService commentService;
    /**
     * @title 用户发布活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    public int publishActivity(PublishActivity request){
        return activityDao.publishActivity(request);
    }

    /**
     * @title 用户删除活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    public int deleteActivity(int activityId){
        return activityDao.deleteActivity(activityId);
    }

    /**
     * 获取最新发布的活动
     * @return
     */
    public List<Activity> selectActivityListByTime(){
        List<Activity> activityList=activityDao.selectActivityListByTime();
        for(Activity activity:activityList){
            List<String> list= Arrays.asList(activity.getImages().split(","));
            activity.setImageList(list);
        }
        return activityList;
    }

    /**
     * @title 获取该活动的详情内容
     * @description 获取该活动的详情内容
     * @author lidongming 
     * @updateTime 2020/4/4 5:01 
     */
    public ActivityDetail selectActivityDetail(int activityId,int userId){
        ActivityDetail activityDetail=activityDao.selectActivityDetail(activityId, userId);
        activityDetail.setActivityCommentList(commentService.getActivityCommentList(activityId,0));
        return activityDetail;
    }

    /**
     * 用户首次进入活动详情页，浏览量+1(先从redis判断)
     * @param activityId
     */
    public void clickActivityDetail(int activityId,int userId){
        
    }

    /**
     * 用户申请加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int tryJoinActivity(int activityId,int userId){
        return activityDao.tryJoinActivity(activityId, userId);
    }

    /**
     * 用户取消加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int cancelJoinActivity(int activityId,int userId){
        return activityDao.cancelJoinActivity(activityId, userId);
    }

    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int agreeJoinActivity(int activityId,int userId){
        return activityDao.agreeJoinActivity(activityId, userId);
    }

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    public int disagreeJoinActiviy(int activityId,int userId){
        return activityDao.disagreeJoinActivity(activityId, userId);
    }

}
