package com.ldm.service.dynamic;

import com.ldm.entity.dynamic.Dynamic;
import com.ldm.request.PublishDynamicRequest;

import java.util.List;

public interface DynamicService {
    void publish(PublishDynamicRequest publishDynamicRequest);
    List<Dynamic> selectDynamicList(String userId);
}
