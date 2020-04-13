package com.ldm.service;

import com.ldm.dao.ActivityDao;
import com.ldm.dao.DynamicDao;
import com.ldm.entity.Activity;
import com.ldm.entity.Dynamic;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
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

    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            // 通过key获取存储于redis中的对象（这个对象是以json格式存储的，所以是字符串）
            String strValue = jedis.get(key);
            // 将json字符串转换为对应的对象
            T objValue = JsonUtil.stringToBean(strValue, clazz);
            return objValue;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
        
    }

    public <T> boolean set(String key, T value) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            // 将对象转换为json字符串
            String strValue = JsonUtil.beanToString(value);
            if (strValue == null || strValue.length() <= 0){
                return false;
            }
            jedis.set(key, strValue);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
        return true;
    }

    /**
     * @title set
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/12 0:56 
     */
    public <T> boolean sadd(String key, T value) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            // 将对象转换为json字符串
            String strValue = JsonUtil.beanToString(value);
            if (strValue == null || strValue.length() <= 0){
                return false;
            }
            jedis.sadd(key, strValue);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
        return true;
    }

    /**
     * @title 当用户删除活动/动态/评论等,将所有与之相关并存储在redis中的都删除
     * @description
     * @author lidongming
     * @updateTime 2020/4/9 23:08
     */
    public <T> boolean mdel(String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            Set<String> set=jedis.smembers(key);
            for(String string:set){
                jedis.del(string);
            }
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
        return true;
    }



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

    public boolean exists(String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.exists(key);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    public long incr(String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.incr(key);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    public long decr(String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.decr(key);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }
    public long lpush(String key, String value) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.lpush(key, value);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.brpop(timeout, key);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    public List<String> lrange(String key){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.lrange(key,0,-1);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
    }

    /**
     * @title hash字典
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/12 0:55 
     */
    public String hget(String key,String field){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.hget(key,field);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
    }
    public Long hset(String key,String field,String value){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.hset(key, field, value);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
    }

    public boolean delete(String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            Long del = jedis.del(key);
            return del > 0;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    public Long zadd(String key,String member) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.zadd(key,System.currentTimeMillis(),member);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * @title 移除有序集合中的一个或多个成员
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/12 1:08 
     */
    public Long zrem(String key,String member) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.zrem(key,member);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * @title 获取有序集合的成员数
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/12 1:03 
     */
    public Long zcard(String key) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return jedis.zcard(key);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }



    /**
     * 点赞帖子
     * @param dynamicId
     * @param userId
     * @return
     */
    public void likeDynamic(int dynamicId,int userId){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String key="like:dynamic:"+dynamicId;
            if(jedis.sismember(key,""+userId)) {
                jedis.srem(key,""+userId);
            }
            else {
                jedis.sadd(key,""+userId);
            }
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
            String key=RedisKeyUtil.limitFrequency(type, userId);
            jedis.zadd(key,nowTs,""+nowTs);
            jedis.zremrangeByScore(key,0,nowTs-period*1000);
            return jedis.zcard(key)>maxCount;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * 排行榜功能
     * @param
     */
    public void rank(){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String key="rank:activity";
            Set<String> set=jedis.zrevrange(key,0,49);
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
    private void returnToPool(Jedis jedis) {
        if (jedis != null){
            jedis.close();
        }
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("redis初始化开始咯!!!");
        List<SimpleUserInfo> simpleUserInfoList=userService.selectSimpleUserInfo();
        List<Activity> activityList=activityDao.selectActivityListByTime(0,1000000);
        List<Dynamic> dynamicList=dynamicDao.selectAllDynamic();
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            jedis.flushDB();// 删除当前数据库中的所有Key
            for(SimpleUserInfo simpleUserInfo:simpleUserInfoList){
                jedis.hset(RedisKeyUtil.getUserInfo(simpleUserInfo.getUserId()),"avatar",simpleUserInfo.getAvatar());
                jedis.hset(RedisKeyUtil.getUserInfo(simpleUserInfo.getUserId()),"userNickname",simpleUserInfo.getUserNickname());
            }
            for (Activity activity:activityList){
                jedis.hset(RedisKeyUtil.getActivityInfo(activity.getActivityId()),"userId",String.valueOf(activity.getUserId()));
                List<String> imageList= Arrays.asList(activity.getImages().split(","));
                jedis.hset(RedisKeyUtil.getActivityInfo(activity.getActivityId()),"image",imageList.get(0));
            }
            for (Dynamic dynamic:dynamicList){
                jedis.hset(RedisKeyUtil.getDynamicInfo(dynamic.getDynamicId()),"userId",String.valueOf(dynamic.getUserId()));
                List<String> imageList= Arrays.asList(dynamic.getImages().split(","));
                jedis.hset(RedisKeyUtil.getDynamicInfo(dynamic.getDynamicId()),"image",imageList.get(0));
            }

            log.debug("redis初始化成功!!!");
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
    }
}