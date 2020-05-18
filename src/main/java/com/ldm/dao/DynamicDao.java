package com.ldm.dao;

import com.ldm.entity.DynamicIndex;
import com.ldm.entity.DynamicDetail;
import com.ldm.entity.RedisUserId;
import com.ldm.request.PublishDynamic;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface DynamicDao {

    // 用户redis初始化
    @Select("select * from t_dynamic")
    List<DynamicIndex> selectAllDynamic();

    // 用于同步redis,保证数据一致性(注意索引顺序)
    @Select("SELECT user_id FROM t_like WHERE user_id=#{userId} AND dynamic_id=#{dynamicId}")
    RedisUserId isLikeDynamic(int userId, int dynamicId);

    @Select("SELECT user_id FROM t_like WHERE dynamic_id=#{dynamicId}")
    RedisUserId isExistDynamic(int dynamicId);

    // 获取朋友圈动态(代码在xml里)
    List<DynamicIndex> selectDynamicList(@Param("idList") List<Integer> dynamicIdList, int pageNum, int pageSize);

    // 获取我发表的动态列表
    @Select("SELECT * FROM t_dynamic WHERE user_id=#{userId} ORDER BY dynamic_id " +
            "DESC LIMIT #{pageNum},#{pageSize}")
    List<DynamicIndex> selectDynamicCreatedByMeList(int userId, int pageNum, int pageSize);

    // 获取动态详情
    @Select("SELECT * FROM t_dynamic WHERE dynamic_id=#{dynamicId}")
    DynamicDetail selectDynamicDetail(int dynamicId);

    // 发表动态
    @Insert("INSERT INTO t_dynamic VALUES(NULL,#{content}, #{images}, #{publishLocation}, #{userId},0, 0, NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "dynamicId",keyColumn = "dynamic_id")
    int publishDynamic(PublishDynamic request);

    // 将所有与动态相关的都删除
    @Delete({"DELETE FROM t_dynamic WHERE dynamic_id=#{dynamicId};",
            "DELETE FROM t_comment WHERE item_id=#{dynamicId} AND flag=1;",
            "DELETE FROM t_reply WHERE comment_id IN (SELECT comment_id FROM t_comment WHERE item_id=#{dynamicId} and flag=1);",
            "DELETE FROM t_like WHERE dynamic_id=#{dynamicId};"})
    int deleteDynamic(int dynamicId);

    // 点赞动态
    @Insert("INSERT INTO t_like VALUES (NULL,#{dynamicId}, #{userId}, #{toUserId}, NOW());" +
            "UPDATE t_dynamic SET like_count=like_count+1 WHERE dynamic_id=#{dynamicId}")
    int likeDynamic(int dynamicId,int userId,int toUserId);

    // 取消点赞动态
    @Update("DELETE FROM t_like WHERE dynamic_id=#{dynamicId} AND user_id=#{userId};" +
            "UPDATE t_dynamic SET like_count=like_count-1 WHERE dynamic_id=#{dynamicId}")
    int cancelLikeDynamic(int dynamicId,int userId);
}