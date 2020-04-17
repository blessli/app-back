package com.ldm.dao;

import com.ldm.entity.SimpleUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface UserDao {

    /**
     * @title 用户redis初始化
     * @description 将用户基本信息保存在redis中
     * @author lidongming
     * @updateTime 2020/4/15 13:05
     */
    @Select("SELECT user_id,user_nickname,avatar FROM `t_user`")
    List<SimpleUserInfo> selectSimpleUserInfo();

}
