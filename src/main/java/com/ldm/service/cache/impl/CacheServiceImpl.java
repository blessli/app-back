package com.ldm.service.cache.impl;

import com.ldm.service.cache.CacheService;
import com.ldm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service(value = "/cacheService")
public class CacheServiceImpl implements CacheService {
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    @Override
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

    @Override
    public <T> boolean set(String key, T value) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
        // 将对象转换为json字符串
        String strValue = JsonUtil.beanToString(value);
        if (strValue == null || strValue.length() <= 0)
            return false;
        jedis.set(key, strValue);
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
    @Override
    public <T> boolean set(String key, T value, String nxxx, String expx, int expireSeconds) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            // 将对象转换为json字符串
            String strValue = JsonUtil.beanToString(value);
            if (strValue == null || strValue.length() <= 0)
                return false;
            jedis.set(key,strValue,nxxx,expx,expireSeconds);
            return true;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    @Override
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

    @Override
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

    @Override
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
    @Override
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
    @Override
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

    @Override
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

    /**
     * 点赞帖子
     * @param dynamicId
     * @param userId
     * @return
     */
    @Override
    public void likeDynamic(int dynamicId,int userId){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String key="like:dynamic:"+dynamicId;
            if(jedis.sismember(key,""+userId)) jedis.srem(key,""+userId);
            else jedis.sadd(key,""+userId);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * 用户操作频率限制,如发帖
     * @param userId
     * @return
     */
    @Override
    public boolean limitFrequency(int userId){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            long nowTs=System.currentTimeMillis();
            int period=60,maxCount=5;
            String key="frequency:limit:"+userId;
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
    @Override
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
     * 某个用户最近的浏览记录
     * @param userId
     */
    @Override
    public void recentScanHistory(int userId){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String key="activity:detail:page:"+userId;
            jedis.lrange(key,0,10);
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * 用户进入详情页
     * @param activityId
     * @param userId
     * @return
     */
    @Override
    public boolean enterDetailPage(int activityId, int userId) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String key="activity:detail:page:"+userId;
            jedis.lrem(key,0,String.valueOf(activityId));
            jedis.lpush(key,String.valueOf(activityId));
            return true;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    /**
     * 某个用户最近的历史搜索记录
     * @param userId
     * @return
     */
    @Override
    public boolean recentSearchHistory(int userId) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String key="activity:detail:page:"+userId;
            return false;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    @Override
    public boolean addFollowsFansById(int fromId, int toId) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return false;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }

    @Override
    public boolean deleteFollowsFansById(int fromId, int toId) {
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            return false;
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }
    public String testLock(){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            String result=jedis.set("lock-token","lidongming","NX","EX",60*2);
            return result;// "OK"为成功
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }
    public Object testUnLock(){
        Jedis jedis = null;// redis连接
        try {
            jedis=jedisPool.getResource();
            //lua script
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            String request= UUID.randomUUID().toString();
            Object result=jedis.eval(script, Collections.singletonList("lock-token"),Collections.singletonList("lidongming"));
            return result;// 1为成功
        }finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }

    }
    /**
     * 将redis连接对象归还到redis连接池
     *
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null)
            jedis.close();
    }
}