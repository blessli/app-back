package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.dao.DynamicDao;
import com.ldm.entity.Activity;
import com.ldm.entity.DynamicIndex;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * @author lidongming
 * @ClassName CacheService.java
 * @Description 缓存服务
 * @createTime 2020年04月04日 05:05:00
 */
@Slf4j
@Service
public class CacheService implements InitializingBean {
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private DynamicDao dynamicDao;


    /**
     * 分布式锁
     * @param key    键
     * @param value  值
     * @param nxxx NX|XX，NX=Only set the key if it does not already exist；XX=Only set the key if it already exist
     * @param expx EX|PX，expire time units: EX = seconds; PX = milliseconds
     * @param expireSeconds
     * @param <T>
     * @return
     */
    public <T> boolean lock(String key, T value, String nxxx, String expx, int expireSeconds) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            // 将对象转换为json字符串
            String strValue = JsonUtil.beanToString(value);
            if (strValue == null || strValue.length() <= 0){
                return false;
            }
            if(jedis.set(key,strValue,nxxx,expx,expireSeconds)=="OK"){
                return true;
            }
            return true;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }
    /**
     * @title 解锁,使用lua脚本保证原子性
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/9 17:42
     */
    public Object unLock(String key){

        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            //lua script
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            String request= UUID.randomUUID().toString();
            Object result=jedis.eval(script, Collections.singletonList("lock-token"),Collections.singletonList("key"));
            return result;// 1为成功
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
    }

    /**
     * @title 用户操作频率限制
     * @description 用于控制用户行为,如发布活动/动态/评论/回复/聊天
     * @author lidongming
     * @updateTime 2020/4/8 1:57
     */
    public boolean limitFrequency(String type,int userId){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            long nowTs=System.currentTimeMillis();
            int period=60,maxCount=10;
            String key= RedisKeys.limitFrequency(type, userId);
            jedis.zadd(key,nowTs,""+nowTs);
            jedis.zremrangeByScore(key,0,nowTs-period*1000);
            return jedis.zcard(key)>maxCount;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * @title 将redis连接对象归还到redis连接池
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/4 16:14
     */
    public static void returnToPool(Jedis jedis) {
        if (jedis != null){
            jedis.close();
        }
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("redis初始化开始!!!");
        List<SimpleUserInfo> simpleUserInfoList=userService.selectSimpleUserInfo();
        List<Activity> activityList=activityDao.selectActivityListByTime(0,1000000);
        List<DynamicIndex> dynamicIndexList =dynamicDao.selectAllDynamic();
        Jedis jedis = jedisPool.getResource();// redis连接
        jedis.flushDB();// 删除当前数据库中的所有Key
        for(SimpleUserInfo simpleUserInfo:simpleUserInfoList){
            jedis.hset(RedisKeys.userInfo(simpleUserInfo.getUserId()),"avatar",simpleUserInfo.getAvatar());
            jedis.hset(RedisKeys.userInfo(simpleUserInfo.getUserId()),"userNickname",simpleUserInfo.getUserNickname());
        }
        for (Activity activity:activityList){
            jedis.hset(RedisKeys.activityInfo(activity.getActivityId()),"userId",String.valueOf(activity.getUserId()));
        }
        for (DynamicIndex dynamicIndex : dynamicIndexList){
            jedis.hset(RedisKeys.dynamicInfo(dynamicIndex.getDynamicId()),"userId",String.valueOf(dynamicIndex.getUserId()));
            List<String> imageList= Arrays.asList(dynamicIndex.getImages().split(","));
            jedis.hset(RedisKeys.dynamicInfo(dynamicIndex.getDynamicId()),"image",imageList.get(0));
        }

        log.debug("redis初始化成功!!!");
        returnToPool(jedis);
    }
}