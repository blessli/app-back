package com.ldm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface UserDao {
    List<Integer> getFollowedUserList(int userId);

}
