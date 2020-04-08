package com.ldm.service;

import com.ldm.dao.CommentDao;
import com.ldm.entity.Comment;
import com.ldm.entity.Reply;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lidongming
 * @ClassName CommentService.java
 * @Description 评论，回复服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;
    /**
     * @title 获取回复列表
     * @description 活动详情页中点击某个评论展示回复列表
     * @author lidongming
     * @updateTime 2020/4/6 19:28
     */
    public List<Reply> getActivityReplyList(int commentId){
        return commentDao.selectReplyList(commentId);
    }
    /**
     * @title 获取评论列表
     * @description 活动/动态详情页中展示评论列表，flag为0则活动，flag为1则动态
     * @author lidongming
     * @updateTime 2020/4/6 19:29
     */
    public List<Comment> getActivityCommentList(int itemId, int flag){
        return commentDao.selectCommentList(itemId, flag);
    }
    /**
     * @title 发表评论
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int publishComment(PublishComment request){
        return commentDao.publishComment(request);
    }
    /**
     * @title 发表回复
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int publishReply(PublishReply request){
        return commentDao.publishReply(request);
    }
    /**
     * @title 删除评论
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    public int deleteComment(int commentId){
        return commentDao.deleteComment(commentId);
    }
    /**
     * @title 删除回复
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:13
     */
    public int deleteReply(int replyId){
        return commentDao.deleteReply(replyId);
    }
}
