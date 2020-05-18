package com.ldm.dao;

import com.ldm.entity.SimpleUserInfo;
import com.ldm.request.UserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface UserDao {

    // redis初始化
    @Select("SELECT * FROM `t_user`")
    List<SimpleUserInfo> selectSimpleUserInfo();

    // 首次登录,添加用户信息
    @Options(useGeneratedKeys = true,keyProperty = "userId",keyColumn = "user_id")
    @Insert("INSERT INTO t_user VALUES(NULL,#{nickName},#{avatarUrl},#{gender},0,0,#{openId})")
    int addUserInfo(UserInfo userInfo);

    // 非首次登录,更新用户信息
    @Update("UPDATE t_user SET user_nickname=#{nickName},avatar=#{avatarUrl},gender=#{gender} WHERE open_id=#{openId}")
    int updateUserInfo(UserInfo userInfo);
}
