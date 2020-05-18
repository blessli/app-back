> 系统要限定用户的某个行为在指定的时间里，只能允许发生 N 次
- 解决方案

这个限流需求中存在一个滑动时间窗口，想想 zset 数据结构的 score 值，是不是可以
通过 score 来圈出这个时间窗口来。而且我们只需要保留这个时间窗口，窗口之外的数据都
可以砍掉。那这个 zset 的 value 填什么比较合适呢？它只需要保证唯一性即可，用 uuid 会 比较浪费空间，那就改用毫秒时间戳吧。
- 代码实现
```java
public class CacheServiceImpl implements CacheService {
    /**
     * 用户操作频率限制,如点赞、发布活动、评论
     * @param userId
     * @return
     */
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
}
```
- 时间复杂度

时间复杂度： O(log(N)+M)， N 为有序集的基数，而 M 为被移除成员的数量。
移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。