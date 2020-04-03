package com.ldm.service;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamic;
import com.ldm.util.DateHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DynamicService{

    @Autowired
    private DynamicDao dynamicDao;
    public void publish(PublishDynamic request) {
        request.setPublishTime(DateHandle.currentDate());
        System.out.println(request.toString());
        dynamicDao.publishDynamic(request);
    }

    public List<Dynamic> selectDynamicList(int userId) {
        List<Dynamic> dynamicList=dynamicDao.selectDynamicList(userId);
        for (Dynamic dynamic: dynamicList){
            dynamic.setImageList(Arrays.asList(dynamic.getImages().split(",")));
        }
        return dynamicList;
    }
}
