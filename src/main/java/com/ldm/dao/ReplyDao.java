package com.ldm.dao;

import com.ldm.entity.Reply;
import com.ldm.request.PublishReply;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ReplyDao {
    /**
     * @title 发表回复
     * @description 评论的回复量+1
     * @author lidongming
     * @updateTime 2020/4/9 23:45
     */
    @Insert("INSERT INTO `t_reply`(`comment_id`, `from_user_id`, `to_user_id`," +
            " `content`, `publish_time`,item_id,flag,to_content) " +
            "VALUES(#{commentId}, #{userId}, #{toUserId}, #{content}, NOW(),#{itemId},#{flag},#{toContent});" +
            "UPDATE t_comment SET reply_count=reply_count+1 WHERE comment_id=#{commentId}")
    int publishReply(PublishReply request);

    /**
     * @title 删除回复
     * @description 评论的回复量-1
     * @author lidongming
     * @updateTime 2020/4/9 23:48
     */
    @Delete("DELETE FROM t_reply WHERE reply_id=#{replyId};" +
            "UPDATE t_comment SET reply_count=reply_count-1 WHERE comment_id=#{commentId}")
    int deleteReply(int commentId,int replyId);

    /**
     * @title 获取评论的回复列表
     * @description
     * @author ggh
     * @updateTime 2020/4/14 19:41
     */
    @Select("SELECT * FROM t_reply WHERE comment_id=#{commentId} ORDER BY " +
            "publish_time DESC LIMIT #{pageNum},#{pageSize}")
    List<Reply> selectReplyList(int commentId, int pageNum, int pageSize);
}
