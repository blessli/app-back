package com.ldm.dao;

import com.ldm.entity.Dynamic;
import com.ldm.entity.DynamicDetail;
import com.ldm.entity.LikeNotice;
import com.ldm.request.PublishDynamic;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface DynamicDao {
    /**
     * @title 发表动态
     * @description 发表动态
     * @author lidongming
     * @updateTime 2020/4/4 4:36
     */
    @Insert("INSERT INTO `t_dynamic`(`content`, `images`, `publish_location`, `user_id`, " +
            "`comment_count`, `like_count`, `dynamic_score`, `publish_time`) " +
            "VALUES (#{content}, #{images}, #{publishLocation}, #{userId}, " +
            "0, 0, 0, NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "dynamicId",keyColumn = "dynamic_id")
    int publishDynamic(PublishDynamic request);


    /**
    *@Author: ggh
    *@Description: 获取已关注者发表的动态
    *@DATE: 2020/4/13 11:47
    *@Param: [userId, pageNum, pageSize]:用户id，起始页数，页面大小
    *@return: java.util.List<com.ldm.entity.Dynamic>
    **/
    @Select("SELECT t_dynamic.*,t_user.avatar,t_user.user_nickname,IFNULL(t_dynamic_like.id,-1) AS is_like FROM `t_follow` INNER JOIN t_dynamic\n" +
            "ON t_follow.follower_id=#{userId} AND t_dynamic.user_id=t_follow.user_id\n" +
            "LEFT JOIN t_user ON t_user.user_id=t_follow.user_id\n" +
            "LEFT JOIN t_dynamic_like ON t_dynamic.dynamic_id=t_dynamic_like.dynamic_id AND #{userId}=t_dynamic_like.user_id\n" +
            "ORDER BY publish_time DESC LIMIT #{pageNum}, #{pageSize}")
    List<Dynamic> selectDynamicList(List<Integer> userIdList, int pageNum,int pageSize);

    /**
    *@Author: ggh
    *@Description: 获取我的动态列表
    *@DATE: 2020/4/13 11:48
    *@Param: [userId, pageNum, pageSize]:用户id，起始页数，页面大小
    *@return: java.util.List<com.ldm.entity.Dynamic>
    **/
    @Select("SELECT t_dynamic.*,avatar,user_nickname,IFNULL(t_dynamic_like.id,-1) AS is_like FROM t_dynamic\n" +
            "LEFT JOIN t_user ON t_user.user_id=t_dynamic.user_id\n" +
            "LEFT JOIN t_dynamic_like ON t_dynamic.dynamic_id=t_dynamic_like.dynamic_id AND #{userId}=t_dynamic_like.user_id\n" +
            "ORDER BY publish_time DESC LIMIT #{pageNum}, #{pageSize}")
    List<Dynamic> selectMyDynamicList(int userId, int pageNum, int pageSize);
    /**
     * @title 删除动态
     * @description 将所有与动态相关的都删除
     * @author lidongming 
     * @updateTime 2020/4/6 15:37 
     */
    @Delete("DELETE from t_reply where comment_id in (SELECT t_comment.comment_id from t_comment WHERE item_id=#{dynamicId} AND flag=1);\n" +
            "DELETE FROM t_comment WHERE item_id=#{dynamicId} AND flag=1;\n" +
            "DELETE FROM t_dynamic_like WHERE dynamic_id=#{dynamicId};\n" +
            "DELETE FROM t_dynamic WHERE dynamic_id=#{dynamicId};")
    int deleteDynamic(int dynamicId);


    /**
     * @title 点赞动态
     * @description 给动态点赞
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    @Update("INSERT INTO t_dynamic_like VALUES(NULL,#{dynamicId},#{userId},NOW(),#{image});\n" +
            "UPDATE t_dynamic SET like_count=like_count+1 WHERE dynamic_id=#{dynamicId}")
    int likeDynamic(int dynamicId,int userId,String image);
    /**
     * @title 取消点赞动态
     * @description 取消给动态点赞
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    @Update("DELETE FROM t_dynamic_like WHERE dynamic_id=#{dynamicId} AND user_id=#{userId};" +
            "UPDATE t_dynamic SET like_count=like_count-1 WHERE dynamic_id=#{dynamicId}")
    int cancelLikeDynamic(int dynamicId,int userId);

    /**
     * @title 检查是否已经点赞
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/9 22:28 
     */
    @Select("SELECT COUNT(id) FROM t_dynamic_like WHERE dynamic_id=#{dynamicId} AND user_id=#{userId}")
    int checkLiked(int dynamicId,int userId);
    /**
     * @title 获取某个动态详情
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/10 20:24
     */
    @Select("SELECT t_dynamic.*,avatar,user_nickname,IFNULL(t_dynamic_like.id,-1) AS is_like FROM t_dynamic\n" +
            "LEFT JOIN t_user ON t_user.user_id=t_dynamic.user_id\n" +
            "LEFT JOIN t_dynamic_like ON t_dynamic.dynamic_id=t_dynamic_like.dynamic_id AND #{userId}=t_dynamic_like.user_id\n" +
            "WHERE t_dynamic.dynamic_id=#{dynamicId}")
    DynamicDetail selectDynamicDetail(int dynamicId,int userId);

    /**
     * @title 获取点赞通知
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/11 13:31 
     */
    /**
    *@Author: ggh
    *@Description: 获取点赞通知
    *@DATE: 2020/4/13 11:49
    *@Param: [userId, pageNum, pageSize]:用户id，起始页数，页面大小
    *@return: java.util.List<com.ldm.entity.LikeNotice>
    **/
    @Select("SELECT t.dynamic_id,t.user_id,t.publish_time,avatar,user_nickname,image FROM t_dynamic_like t\n" +
            "LEFT JOIN t_user ON t_user.user_id=t.user_id\n" +
            "INNER JOIN t_dynamic ON t_dynamic.dynamic_id=t.dynamic_id AND t_dynamic.user_id=#{userId} LIMIT #{pageNum}, #{pageSize}")
    List<LikeNotice> selectLikeNotice(int userId, int pageNum, int pageSize);


    @Select("select * from t_dynamic")
    List<Dynamic> selectAllDynamic();
}
