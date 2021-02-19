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
     * @author lidongming
     * @updateTime 2020/4/4 4:36
     */
    @Insert("INSERT INTO t_dynamic(content,images,publish_location,user_id,publish_time,status) " +
            "VALUES(#{content},#{images},#{publishLocation},#{userId},NOW(),1)")
    @Options(useGeneratedKeys = true,keyProperty = "dynamicId",keyColumn = "dynamic_id")
    int publishDynamic(PublishDynamic request);


    /**
     * @title 获取朋友圈动态
     * @description 根据传入的dynamicIdList进行in查询,走xml
     * @author ggh
     * @updateTime 2020/4/14 17:54
     */
    List<DynamicIndex> selectDynamicList(int userId,@Param("dynamicIdList") List<Integer> dynamicIdList, int pageNum, int pageSize);

    /**
     * @title 获取我发表的动态列表
     * @description 无需关联t_dynamic_like和t_user来进行查询,走redis
     * @author ggh
     * @updateTime 2020/4/14 17:58
     */
    @Select("SELECT * FROM t_dynamic WHERE user_id=#{userId} and status=1 ORDER BY publish_time " +
            "DESC LIMIT #{pageNum},#{pageSize}")
    List<DynamicIndex> selectDynamicCreatedByMeList(int userId, int pageNum, int pageSize);
    /**
     * @title 删除动态
     * @description 将所有与动态相关的都删除
     * @author lidongming 
     * @updateTime 2020/4/6 15:37 
     */
    @Update("UPDATE t_dynamic SET status=0 where dynamic_id=#{dynamicId}")
    int deleteDynamic(int dynamicId);

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
     * @description 只需要查t_dynamic表
     * @author ggh
     * @updateTime 2020/4/10 20:24
     */
    @Select("SELECT * FROM t_dynamic WHERE dynamic_id=#{dynamicId}")
    DynamicDetail selectDynamicDetail(int dynamicId);

    @Select("select * from t_dynamic")
    List<DynamicIndex> selectAllDynamic();
}
