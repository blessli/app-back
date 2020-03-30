package com.ldm.dao;

import com.ldm.entity.Dynamic;
import com.ldm.request.PublishDynamicRequest;

import java.util.List;

public interface DynamicDao {
    int publish(PublishDynamicRequest publishDynamicRequest);
    List<Dynamic> selectDynamicList(String userId);
}
