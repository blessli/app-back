package com.ldm.service;

import com.ldm.dao.CommentDao;
import com.ldm.request.PublishComment;
import com.ldm.request.PublishReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @title 发表评论
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    int publishComment(PublishComment request){
        return commentDao.publishComment(request);
    }
    /**
     * @title 发表回复
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    int publishReply(PublishReply request){
        return commentDao.publishReply(request);
    }
    /**
     * @title 删除评论
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:12
     */
    int deleteComment(int commentId){
        return commentDao.deleteComment(commentId);
    }
    /**
     * @title 删除回复
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 5:13
     */
    int deleteReply(int replyId){
        return commentDao.deleteReply(replyId);
    }
}
