package com.ldm.dao;


import com.ldm.entity.Comment;
import com.ldm.request.PublishComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface CommentDao {

    // 获取评论列表
    @Select("SELECT * FROM t_comment WHERE item_id=#{itemId} AND flag=#{flag} " +
            "ORDER BY comment_id DESC LIMIT #{pageNum},#{pageSize}")
    List<Comment> selectCommentList(int itemId,int flag, int pageNum,int pageSize);

    // 发表评论
    @Insert("INSERT INTO t_comment VALUES(NULL,#{itemId}, #{userId}, NOW(), #{content}, 0, #{flag})")
    int publishComment(PublishComment request);

    // 删除评论,删除评论表和回复表中对应的数据,并更新对应活动/动态的评论量
    @Delete("DELETE FROM t_comment WHERE comment_id=#{commentId};" +
            "DELETE FROM t_reply WHERE comment_id=#{commentId}")
    int deleteComment(int commentId);

    // 删除评论时,活动的评论量-1
    @Update("UPDATE t_activity SET comment_count=comment_count-1 WHERE activity_id=#{itemId}")
    int reduceActivityCommentCount(int itemId);

    // 删除评论时,动态的评论量-1
    @Update("UPDATE t_dynamic SET comment_count=comment_count-1 WHERE dynamic_id=#{itemId}")
    int reduceDynamicCommentCount(int itemId);

    // 删除评论时,活动的评论量+1
    @Update("UPDATE t_activity SET comment_count=comment_count+1 WHERE activity_id=#{itemId}")
    int addActivityCommentCount(int itemId);

    // 发表评论时,动态的评论量+1
    @Update("UPDATE t_dynamic SET comment_count=comment_count+1 WHERE dynamic_id=#{itemId}")
    int addDynamicCommentCount(int itemId);



}