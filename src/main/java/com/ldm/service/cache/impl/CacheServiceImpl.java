package com.ldm.service.cache.impl;

import com.ldm.service.cache.CacheService;
import com.ldm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

@RestController
public class CacheServiceImpl implements CacheService {

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;// redis连接

        try {
            jedis = jedisPool.getResource();
            // 通过key获取存储于redis中的对象（这个对象是以json格式存储的，所以是字符串）
            String strValue = jedis.get(key);
            // 将json字符串转换为对应的对象
            T objValue = JsonUtil.stringToBean(strValue, clazz);
            return objValue;
        } finally {
            // 归还redis连接到连接池
            returnToPool(jedis);
        }
    }

    @Override
    public <T> boolean set(String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 将对象转换为json字符串
            String strValue = JsonUtil.beanToString(value);
            if (strValue == null || strValue.length() <= 0)
                return false;
            jedis.set(key, strValue);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public <T> boolean set(String key, T value, String nxxx, String expx, int expireSeconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 将对象转换为json字符串
            String strValue = JsonUtil.beanToString(value);
            if (strValue == null || strValue.length() <= 0)
                return false;
            //jedis.set(key,strValue,nxxx,expx,expireSeconds);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incr(key);
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.decr(key);
        } finally {
            returnToPool(jedis);
        }
    }
    @Override
    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key, value);
        }finally {
            returnToPool(jedis);
        }
    }
    @Override
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout, key);
        } finally {
            returnToPool(jedis);
        }
        return null;
    }

    @Override
    public boolean delete(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long del = jedis.del(key);
            return del > 0;
        } finally {
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key="like:dynamic:"+dynamicId;
            if(jedis.sismember(key,""+userId)) jedis.srem(key,""+userId);
            else jedis.sadd(key,""+userId);
        } finally {
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long nowTs=System.currentTimeMillis();
            int period=60,maxCount=5;
            String key="frequency:limit:"+userId;
            jedis.zadd(key,nowTs,""+nowTs);
            jedis.zremrangeByScore(key,0,nowTs-period*1000);
            return jedis.zcard(key)>maxCount;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 排行榜功能
     * @param
     */
    @Override
    public void rank(){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key="rank:activity";
            Set<String> set=jedis.zrevrange(key,0,49);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 某个用户最近的浏览记录
     * @param userId
     */
    @Override
    public void recentScanHistory(int userId){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key="activity:detail:page:"+userId;
            jedis.lrange(key,0,10);
        } finally {
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

    /**
     * 用户进入详情页
     * @param activityId
     * @param userId
     * @return
     */
    @Override
    public boolean enterDetailPage(int activityId, int userId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key="activity:detail:page:"+userId;
            jedis.lrem(key,0,String.valueOf(activityId));
            jedis.lpush(key,String.valueOf(activityId));
        } finally {
            returnToPool(jedis);
        }
        return true;
    }

    /**
     * 某个用户最近的历史搜索记录
     * @param userId
     * @return
     */
    @Override
    public boolean recentSearchHistory(int userId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key="activity:detail:page:"+userId;

        } finally {
            returnToPool(jedis);
        }
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

}