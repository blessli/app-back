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

    // 获取申请通知
    @Select("SELECT t1.activity_id,t1.user_id,t2.activity_name,t1.publish_time,t1.apply_status FROM t_apply t1 LEFT JOIN t_activity t2 ON t2.activity_id=t1.activity_id WHERE to_user_id=#{userId} ORDER BY t1.id DESC LIMIT #{pageNum},#{pageSize}")
    List<ApplyNotice> selectApplyNotice(int userId, int pageNum, int pageSize);

    // 获取点赞通知
    @Select("SELECT * FROM `t_like` WHERE to_user_id=#{userId} ORDER BY id DESC LIMIT #{pageNum},#{pageSize}")
    List<LikeNotice> selectLikeNotice(int userId, int pageNum, int pageSize);

    // 获取回复通知
    @Select("SELECT item_id,flag,from_user_id user_id,publish_time,content,to_content FROM `t_reply` WHERE to_user_id=#{userId} ORDER BY reply_id DESC LIMIT #{pageNum},#{pageSize}")
    List<ReplyNotice> selectReplyNotice(int userId, int pageNum, int pageSize);

    // 获取关注通知
    @Select("SELECT t.follower_id AS user_id,t.publish_time FROM `t_follow` t WHERE user_id=#{userId} ORDER BY id DESC LIMIT #{pageNum},#{pageSize}")
    List<FollowNotice> selectFollowNotice(int userId, int pageNum, int pageSize);


}
