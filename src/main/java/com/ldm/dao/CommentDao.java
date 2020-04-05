package com.ldm.dao;


import com.ldm.entity.Comment;
import com.ldm.entity.Reply;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {
    int publishComment(PublishComment request);
    int publishReply(PublishReply request);
    int deleteComment(int commentId);
    int deleteReply(int replyId);
    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming 
     * @updateTime 2020/4/4 5:54 
     */
    List<Comment> selectCommentList(int itemId,int flag);
    /**
     * @title 获取评论的回复列表
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 6:02
     */
    List<Reply> selectReplyList(int commentId);
}
