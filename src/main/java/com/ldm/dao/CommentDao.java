package com.ldm.dao;


import com.ldm.entity.Comment;
import com.ldm.entity.Reply;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentDao {

    /**
     * 发布评论
     * @param request
     * @return
     */
    @Insert("INSERT INTO `t_comment`(`item_id`, `user_id`, " +
            "`publish_time`, `content`, `reply_count`, `flag`) VALUES " +
            "(#{itemId}, #{userId}, #{publishTime}, #{content}, 0, #{flag})")
    int publishComment(PublishComment request);

    /**
     * 回复评论
     * @param request
     * @return  bean里没有flag字段
     */
    @Insert("INSERT INTO `t_reply`(`comment_id`, `from_user_id`, `to_user_id`," +
            " `content`, `publish_time`) " +
            "VALUES (#{commentId}, #{fromUserId}, #{toUserId}, #{content}, #{publishTime})")
    int publishReply(PublishReply request);

    /**
     *删除评论:删除评论表和回复表中对应的数据
     * @param commentId
     * @return
     */
    @Delete({"DELETE FROM t_comment WHERE comment_id=#{commentId}",
    "DELETE FROM t_reply WHERE comment_id=#{commentId}"})
    int deleteComment(int commentId);

    /**
     *删除回复
     * @param replyId
     * @return
     */
    @Delete("DELETE FROM t_reply WHERE replyId=#{replyId}")
    int deleteReply(int replyId);

    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming 
     * @updateTime 2020/4/4 5:54 
     */
    @Select("SELECT t2.*, t1.user_nickname, t1.avatar FROM t_user t1 JOIN " +
            "(SELECT * FROM t_comment WHERE item_id=#{itemId} AND flag=#{flag}) t2 " +
            "ON t1.user_id=t2.user_id")
    List<Comment> selectCommentList(int itemId,int flag);

    /**
     * @title 获取评论的回复列表
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 6:02
     */
    List<Reply> selectReplyList(int commentId);
}
