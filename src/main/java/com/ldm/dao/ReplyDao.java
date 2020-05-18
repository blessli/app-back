
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

    // 获取回复列表
    @Select("SELECT * FROM t_reply WHERE comment_id=#{commentId} ORDER BY " +
            "reply_id DESC LIMIT #{pageNum},#{pageSize}")
    List<Reply> selectReplyList(int commentId, int pageNum, int pageSize);

    // 发表回复,评论的回复量+1,更新t_reply
    @Insert("INSERT INTO `t_reply` VALUES (NULL,#{commentId},#{itemId},#{flag},#{userId}, #{toUserId}, #{content},#{toContent}, NOW());\n" +
            "UPDATE t_comment SET reply_count=reply_count+1 WHERE comment_id=#{commentId}")
    int publishReply(PublishReply request);

    // 删除回复,评论的回复量-1,更新t_reply
    @Delete("DELETE FROM t_reply WHERE reply_id=#{replyId};" +
            "UPDATE t_comment SET reply_count=reply_count-1 WHERE comment_id=#{commentId}")
    int deleteReply(int commentId,int replyId);
}