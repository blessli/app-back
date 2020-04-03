package com.ldm.dao;


import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDao {
    int publishComment(PublishComment request);
    int publishReply(PublishReply request);
    int deleteComment(int commentId);
    int deleteReply(int replyId);
}
