package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.entity.Activity;
import com.ldm.entity.ActivityDetail;
import com.ldm.request.PublishActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    /**
     * @title 用户发布活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    int publishActivity(PublishActivity request){
        return activityDao.publishActivity(request);
    }

    /**
     * @title 用户删除活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:52 
     */
    int deleteActivity(int activityId){
        return activityDao.deleteActivity(activityId);
    }

    /**
     * @title 用户更新活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:58 
     */
    int updateActivity(int activityId){
        return activityDao.updateActivity(activityId);
    }

    /**
     * 获取最新发布的活动
     * @return
     */
    List<Activity> selectActivityListByTime(){
        return activityDao.selectActivityListByTime();
    }

    /**
     * 根据活动类型列表进行筛选活动
     * @param activityTypeList
     * @return
     */
    List<Activity> selectActivityListByActivityType(List<String> activityTypeList){
        return activityDao.selectActivityListByActivityType(activityTypeList);
    }

    /**
     * @title 根据活动的热度进行排序，基于hacker news算法
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:05
     */
    List<Activity> selectActivityListByHot(){
        return activityDao.selectActivityListByHot();
    }
    /**
     * @title 获取该活动的详情内容
     * @description 获取该活动的详情内容
     * @author lidongming 
     * @updateTime 2020/4/4 5:01 
     */
    ActivityDetail selectActivityDetail(int activityId,int userId){
        return activityDao.selectActivityDetail(activityId,userId);
    }

    /**
     * 用户首次进入活动详情页，浏览量+1
     * @param activityId
     */
    void clickActivityDetail(int activityId,int userId){
        
    }

    /**
     * 用户申请加入活动
     * @param activityId
     * @param userId
     * @return
     */
    int tryJoinActivity(int activityId,int userId){
        return activityDao.tryJoinActivity(activityId, userId);
    }

    /**
     * 用户取消加入活动
     * @param activityId
     * @param userId
     * @return
     */
    int cancelJoinActivity(int activityId,int userId){
        return activityDao.cancelJoinActivity(activityId, userId);
    }

    /**
     * 活动发布者同意该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    int agreeJoinActiviy(int activityId,int userId){
        return activityDao.agreeJoinActiviy(activityId, userId);
    }

    /**
     * 活动发布者拒绝该加入活动
     * @param activityId
     * @param userId
     * @return
     */
    int disagreeJoinActiviy(int activityId,int userId){
        return activityDao.disagreeJoinActiviy(activityId, userId);
    }

    /**
     * 活动发布者将某用户从该活动中删除
     * @param activityId
     * @param userId
     * @return
     */
    int deleteJoinedActivity(int activityId,int userId){
        return activityDao.deleteJoinedActivity(activityId, userId);
    }

    /**
     * 活动发布者邀请该用户加入活动
     * @param activityId
     * @param userId
     * @return
     */
    int inviteJoinActivity(int activityId,int userId){
        return activityDao.inviteJoinActivity(activityId, userId);
    }
}
