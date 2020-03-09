package com.ldm.service.cache;

public interface CacheService {
    /**
     * redis 的get操作，通过key获取存储在redis中的对象
     *
     * @param key    业务层传入的key
     * @param clazz  存储在redis中的对象类型（redis中是以字符串存储的）
     * @param <T>    指定对象对应的类型
     * @return 存储于redis中的对象
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * redis的set操作
     *
     * @param key    键
     * @param value  值
     * @return 操作成功为true，否则为false
     */
    <T> boolean set(String key, T value);

    /**
     * redis的set操作
     *
     * @param key    键
     * @param value  值
     * @return 操作成功为true，否则为false
     */
    <T> boolean set(String key, T value, String nxxx, String expx, int expireSeconds);

    /**
     * 判断key是否存在于redis中
     *
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * 自增
     *
     * @param key
     * @return
     */
    long incr(String key);

    /**
     * 自减
     *
     * @param key
     * @return
     */
    long decr(String key);


    /**
     * 删除缓存中的用户数据
     *
     * @param key
     * @return
     */
    boolean delete(String key);
    /**
     * 点赞帖子
     * @param dynamicId
     * @param userId
     * @return
     */
    void likeDynamic(int dynamicId, int userId);

    /**
     * 用户操作频率限制,如发帖
     * @param userId
     * @return
     */
    boolean limitFrequency(int userId);

    /**
     * 活动排行榜
     * @param
     */
    void rank();

    /**
     * 某个用户最近的浏览记录
     * @param userId
     */
    void recentScanHistory(int userId);

    /**
     * 用户进入详情页
     * @param activityId
     * @param userId
     * @return
     */
    boolean enterDetailPage(int activityId, int userId);

    /**
     * 某个用户最近的历史搜索记录
     * @param userId
     * @return
     */
    boolean recentSearchHistory(int userId);

    /**
     * 关注
     * @param fromId
     * @param toId
     * @return
     */
    boolean addFollowsFansById(int fromId, int toId);

    /**
     * 取消关注
     * @param fromId
     * @param toId
     * @return
     */
    boolean deleteFollowsFansById(int fromId, int toId);
}
