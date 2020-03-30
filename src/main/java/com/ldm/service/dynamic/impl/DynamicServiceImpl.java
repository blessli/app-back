package com.ldm.service.dynamic.impl;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamicRequest;
import com.ldm.service.dynamic.DynamicService;
import com.ldm.util.DateHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service(value = "/dynamicService")
public class DynamicServiceImpl implements DynamicService {

    @Autowired
    private DynamicDao dynamicDao;
    @Override
    public void publish(PublishDynamicRequest publishDynamicRequest) {
        publishDynamicRequest.setPublishTime(DateHandle.currentDate());
        System.out.println(publishDynamicRequest.toString());
        dynamicDao.publish(publishDynamicRequest);
    }

    @Override
    public List<Dynamic> selectDynamicList(String userId) {
        List<Dynamic> dynamicList=dynamicDao.selectDynamicList(userId);
        for (Dynamic dynamic: dynamicList){
            dynamic.setImageList(Arrays.asList(dynamic.getImages().split(",")));
        }
        return dynamicList;
    }
}
