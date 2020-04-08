package com.ldm.service;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamic;
import com.ldm.util.DateHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
/**
 * @author lidongming
 * @ClassName DynamicService.java
 * @Description 动态服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class DynamicService{

    @Autowired
    private DynamicDao dynamicDao;
    /**
     * @title 发表动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/7 13:44
     */
    public int publish(PublishDynamic request) {
        request.setPublishTime(DateHandle.currentDate());
        return dynamicDao.publishDynamic(request);
    }
    /**
     * @title 获取已关注者发表的动态
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/7 2:44
     */
    public List<Dynamic> selectDynamicList(int userId) {
        List<Dynamic> dynamicList=dynamicDao.selectDynamicList(userId);
        for (Dynamic dynamic:dynamicList){
            List<String> list=Arrays.asList(dynamic.getImages().split(","));
            dynamic.setImageList(list);
        }
        return dynamicList;
    }
    /**
     * @title 删除动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/7 13:45
     */
    public int deleteDynamic(int dynamicId){
        return dynamicDao.deleteDynamic(dynamicId);
    }
}
