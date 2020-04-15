package com.ldm.dao;

import com.ldm.entity.ChatHistory;
import com.ldm.entity.ChatMsg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ChatDao {
    /**
     * @title 用户发送聊天信息
     * @description msgFlag是标识哪两个人的消息,保证小到大,比如"1:2"
     * @author lidongming
     * @updateTime 2020/4/7 23:30
     */
    @Insert("INSERT INTO t_chat(user_id,msg,to_user_id,publish_time,msg_flag) VALUES(#{userId},#{msg},#{toUserId},NOW(),#{msgFlag})")
    int sendMsg(int userId,String msg,int toUserId,String publishTime,String msgFlag);
    /**
     * @title 获取聊天双方的历史信息
     * @description 保证userId<toUserId
     * @author lidongming
     * @updateTime 2020/4/7 23:33
     */
    @Select("SELECT user_id,to_user_id,msg,publish_time FROM t_chat WHERE msg_flag=#{msgFlag} ORDER BY publish_time ASC LIMIT #{pageNum},#{pageSize}")
    List<ChatHistory> selectChatHistory(String msgFlag, int pageNum,int pageSize);
    /**
     * @title 获取用户的聊天情况
     * @description 与哪些人聊过天,并展示最新的一条消息
     * @author ggh
     * @updateTime 2020/4/7 23:35
     */
    @Select("SELECT t.uid to_user_id,t.msg,t.publish_time,u.user_nickname as to_user_nickname,u.avatar as to_avatar,IFNULL(un_read_count,0) AS un_read_count from (\n" +
            "SELECT id,uid,msg,publish_time FROM (\n" +
            "(SELECT id,to_user_id as uid,msg,publish_time FROM t_chat WHERE user_id = #{userId})\n" +
            "UNION\n" +
            "(SELECT id,user_id as uid,msg,publish_time FROM t_chat WHERE to_user_id = #{userId})\n" +
            "ORDER BY publish_time DESC) as tmp GROUP BY tmp.uid ORDER BY publish_time DESC limit 20\n" +
            ") as t LEFT JOIN t_user as u on t.uid = u.user_id\n" +
            "LEFT JOIN t_chat_unread ON t_chat_unread.to_user_id=t.uid")
    List<ChatMsg> selectChatList(int userId);
    @Update("")
    int updateUnReadCount(int userId,int toUserId);

}
