package com.ldm.dao;

import com.ldm.entity.ApplyNotice;
import com.ldm.entity.FollowNotice;
import com.ldm.entity.LikeNotice;
import com.ldm.entity.ReplyNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface NoticeDao {

    /**
     * @title 获取申请通知
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/16 16:30 
     */
    @Select("SELECT t1.*,activity_name FROM t_join t1 LEFT JOIN t_activity t2 ON t1.activity_id=t2.activity_id WHERE " +
            "to_user_id=#{userId} ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<ApplyNotice> selectApplyNotice(int userId, int pageNum, int pageSize);

    /**
     * @title 获取点赞通知
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/16 16:30 
     */
    @Select("SELECT * FROM `t_like` WHERE to_user_id=#{userId} ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<LikeNotice> selectLikeNotice(int userId, int pageNum, int pageSize);

    /**
     * @title 获取回复通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/16 15:39
     */
    @Select("SELECT * FROM `t_reply` WHERE to_user_id=#{userId} ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<ReplyNotice> selectReplyNotice(int userId, int pageNum, int pageSize);

    /**
     * @title 获取关注通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/16 15:39
     */
    @Select("SELECT t.follower_id AS user_id,t.publish_time FROM `t_follow` t WHERE user_id=#{userId} ORDER BY publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<FollowNotice> selectFollowNotice(int userId, int pageNum, int pageSize);
}
