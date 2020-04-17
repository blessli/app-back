package com.ldm.dao;

import com.ldm.entity.ActivityIndex;
import com.ldm.entity.ActivityDetail;
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
     * @description 返回activityId,注意新增了share_count字段,这里需要更新
     * @author ggh
     * @updateTime 2020/4/4 4:29
     */
    @Options(useGeneratedKeys = true,keyProperty = "activityId",keyColumn = "activity_id")
    int publishActivity(PublishActivity request);

    /**
     * @title 删除活动
     * @description 将所有与活动相关的都删除
     * @author ggh
     * @updateTime 2020/4/4 4:30
     */
    int deleteActivity(int activityId);

    /**
     * @title 获取最新发布的活动
     * @description
     * @author lidongming
     * @updateTime 2020/4/14 19:34
     */
    @Select("SELECT * FROM t_activity ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<ActivityIndex> selectActivityListByTime(int pageNum,int pageSize);



    /**
     * @title 获取该用户申请加入的活动
     * @description 直接走t_apply
     * @author ggh
     * @updateTime 2020/4/14 19:36
     */
    List<MyActivity> selectActivityApplyList(int userId,int pageNum,int pageSize);


    /**
     * @title 获取我发布的活动列表
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:37
     */
    @Select("SELECT * FROM t_activity WHERE user_id=#{userId} ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
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
    @Insert("INSERT INTO t_activity_view VALUES(NULL,#{activityId},#{userId});" +
            "UPDATE t_activity SET view_count=view_count+1 WHERE activity_id=#{activityId}")
    int addViewCount(int activityId,int userId);
    /**
     * @title 用户申请加入活动
     * @description
     * @author ggh
     * @updateTime 2020/4/10 19:51
     */
    int joinActivity(int activityId,int userId);

    /**
     * @title 用户取消加入活动
     * @description
     * @author ggh
     * @updateTime 2020/4/10 19:55
     */
    int exitActivity(int activityId,int userId);

    /**
     * @title 活动发布者同意该用户加入活动
     * @description
     * @author ggh
     * @updateTime 2020/4/10 19:55
     */
    int agreeJoinActivity(int activityId,int userId);

    /**
     * @title 活动发布者拒绝该加入活动
     * @description
     * @author ggh
     * @updateTime 2020/4/10 19:54
     */
    int disagreeJoinActivity(int activityId,int userId);

    /**
     * @title 根据es查询出来的activityId列表,在mysql中查询
     * @description 代码在xml里
     * @author ggh
     * @updateTime 2020/4/14 17:18
     */
    List<ActivityIndex> selectActivityListByEs(List<Integer> activityIdList);

    /**
     * @title 异步更新活动分数
     * @description
     * @author ggh
     * @updateTime 2020/4/16 14:40
     */
    int updateActivityScore(int activityId,double score);
}
