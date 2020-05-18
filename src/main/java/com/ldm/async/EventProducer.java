package com.ldm.async;
import com.ldm.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author javaedge
 * @date 2016/7/30
 */
@Service
public class EventProducer {
    @Autowired
    CacheService cacheService;

    public boolean fireEvent(EventModel eventModel) {
//        try {
//            String json = JSONObject.toJSONString(eventModel);
//            String key = RedisKeyUtil.getEventQueueKey();
//            cacheService.lpush(key, json);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
        return true;
    }
}
