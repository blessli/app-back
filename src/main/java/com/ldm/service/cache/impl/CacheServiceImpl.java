package com.ldm.service.cache.impl;

import com.ldm.service.cache.CacheService;
import com.ldm.util.JsonUtil;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class CacheServiceImpl implements CacheService {
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private JedisCluster jedisCluster;

    @Override
    public <T> T get(String key, Class<T> clazz) {

        // 通过key获取存储于redis中的对象（这个对象是以json格式存储的，所以是字符串）
        String strValue = jedisCluster.get(key);
        // 将json字符串转换为对应的对象
        T objValue = JsonUtil.stringToBean(strValue, clazz);
        return objValue;
    }

    @Override
    public <T> boolean set(String key, T value) {
        // 将对象转换为json字符串
        String strValue = JsonUtil.beanToString(value);
        if (strValue == null || strValue.length() <= 0)
            return false;
        jedisCluster.set(key, strValue);
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
        // 将对象转换为json字符串
        String strValue = JsonUtil.beanToString(value);
        if (strValue == null || strValue.length() <= 0)
            return false;
        jedisCluster.set(key,strValue,nxxx,expx,expireSeconds);
        return true;
    }

    @Override
    public boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    @Override
    public long incr(String key) {
        return jedisCluster.incr(key);
    }

    @Override
    public long decr(String key) {
        return jedisCluster.decr(key);
    }
    @Override
    public long lpush(String key, String value) {
        return jedisCluster.lpush(key, value);
    }
    @Override
    public List<String> brpop(int timeout, String key) {
        return jedisCluster.brpop(timeout, key);
    }

    @Override
    public boolean delete(String key) {
        Long del = jedisCluster.del(key);
        return del > 0;
    }

    /**
     * 点赞帖子
     * @param dynamicId
     * @param userId
     * @return
     */
    @Override
    public void likeDynamic(int dynamicId,int userId){
        String key="like:dynamic:"+dynamicId;
        if(jedisCluster.sismember(key,""+userId)) jedisCluster.srem(key,""+userId);
        else jedisCluster.sadd(key,""+userId);
    }

    /**
     * 用户操作频率限制,如发帖
     * @param userId
     * @return
     */
    @Override
    public boolean limitFrequency(int userId){
        long nowTs=System.currentTimeMillis();
        int period=60,maxCount=5;
        String key="frequency:limit:"+userId;
        jedisCluster.zadd(key,nowTs,""+nowTs);
        jedisCluster.zremrangeByScore(key,0,nowTs-period*1000);
        return jedisCluster.zcard(key)>maxCount;
    }

    /**
     * 排行榜功能
     * @param
     */
    @Override
    public void rank(){
        String key="rank:activity";
        Set<String> set=jedisCluster.zrevrange(key,0,49);
    }

    /**
     * 某个用户最近的浏览记录
     * @param userId
     */
    @Override
    public void recentScanHistory(int userId){
        String key="activity:detail:page:"+userId;
        jedisCluster.lrange(key,0,10);
    }

    /**
     * 用户进入详情页
     * @param activityId
     * @param userId
     * @return
     */
    @Override
    public boolean enterDetailPage(int activityId, int userId) {
        String key="activity:detail:page:"+userId;
        jedisCluster.lrem(key,0,String.valueOf(activityId));
        jedisCluster.lpush(key,String.valueOf(activityId));
        return true;
    }

    /**
     * 某个用户最近的历史搜索记录
     * @param userId
     * @return
     */
    @Override
    public boolean recentSearchHistory(int userId) {
        String key="activity:detail:page:"+userId;
        return false;
    }

    @Override
    public boolean addFollowsFansById(int fromId, int toId) {
        return false;
    }

    @Override
    public boolean deleteFollowsFansById(int fromId, int toId) {
        return false;
    }
    public String testLock(){
        String result=jedisCluster.set("lock-token","lidongming","NX","EX",60*2);
        return result;// "OK"为成功
    }
    public Object testUnLock(){
        //lua script
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String request= UUID.randomUUID().toString();
        Object result=jedisCluster.eval(script, Collections.singletonList("lock-token"),Collections.singletonList("lidongming"));
        return result;// 1为成功
    }
}