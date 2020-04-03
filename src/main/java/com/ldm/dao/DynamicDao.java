package com.ldm.dao;

import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamic;

import java.util.List;

public interface DynamicDao {
    /**
     * @title 发表动态
     * @description 发表动态
     * @author lidongming
     * @updateTime 2020/4/4 4:36
     */
    int publishDynamic(PublishDynamic request);
    /**
     * @title 获取已关注者发表的动态
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 4:34 
     */
    List<Dynamic> selectDynamicList(int userId);
}
