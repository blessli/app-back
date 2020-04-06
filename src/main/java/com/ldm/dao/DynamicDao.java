package com.ldm.dao;

import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamic;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Service;

import java.util.List;
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
            "0, 0, 0, #{publishTime})")
    int publishDynamic(PublishDynamic request);


    /**
     * @title 获取已关注者发表的动态
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:34
     * dynamic_score字段没有，参数不够
     */
    @Select("")
    List<Dynamic> selectDynamicList(int userId);
}
