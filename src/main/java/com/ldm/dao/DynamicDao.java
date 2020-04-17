package com.ldm.dao;

import com.ldm.entity.DynamicIndex;
import com.ldm.entity.DynamicDetail;
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
     * @author ggh
     * @updateTime 2020/4/4 4:36
     */
    @Options(useGeneratedKeys = true,keyProperty = "dynamicId",keyColumn = "dynamic_id")
    int publishDynamic(PublishDynamic request);


    /**
     * @title 获取朋友圈动态
     * @description 根据传入的dynamicIdList进行in查询,代码在xml里
     * @author lidongming
     * @updateTime 2020/4/14 17:54
     */
    List<DynamicIndex> selectDynamicList(List<Integer> dynamicIdList, int pageNum, int pageSize);

    /**
     * @title 获取我发表的动态列表
     * @description 无需关联t_dynamic_like和t_user来进行查询,走redis
     * @author ggh
     * @updateTime 2020/4/14 17:58
     */
    @Select("SELECT * FROM t_dynamic WHERE user_id=#{userId} ORDER BY publish_time " +
            "DESC LIMIT #{pageNum},#{pageSize}")
    List<DynamicIndex> selectDynamicCreatedByMeList(int userId, int pageNum, int pageSize);
    /**
     * @title 删除动态
     * @description 将所有与动态相关的都删除
     * @author ggh
     * @updateTime 2020/4/6 15:37 
     */
    int deleteDynamic(int dynamicId);


    /**
     * @title 点赞动态
     * @description 给动态点赞
     * @author ggh
     * @updateTime 2020/4/8 1:48
     */
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
     * @title 获取某个动态详情
     * @description 只需要查t_dynamic表
     * @author ggh
     * @updateTime 2020/4/10 20:24
     */
    @Select("SELECT * FROM t_dynamic WHERE dynamic_id=#{dynamicId}")
    DynamicDetail selectDynamicDetail(int dynamicId);

    @Select("select * from t_dynamic")
    List<DynamicIndex> selectAllDynamic();

    /**
     * @title 异步更新动态分数
     * @description
     * @author ggh
     * @updateTime 2020/4/17 20:27
     */
    int updateDynamicScore(int dynamicId,double score);
}
