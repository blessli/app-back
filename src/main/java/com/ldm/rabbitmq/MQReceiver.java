package com.ldm.rabbitmq;

import com.ldm.request.PublishDynamic;
import com.ldm.pojo.FollowOrNot;
import com.ldm.service.CacheService;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

import java.util.Set;
@Slf4j
//@Service
public class MQReceiver {

    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;

    /**
     * 动态发布处理队列发现新消息时，取队首消息出队列。
     * 根据消息中的发布者UID，遍历其粉丝列表(当以后全站粉丝量较大时，可扩展为选择性推送)。
     * 给每个粉丝推送一条动态，将动态ID和时间戳写入粉丝的收Feed有序集中。
     * 消息处理完成，检查队列是否还有消息，无则阻塞。
     * @param message
     */
    @RabbitListener(queues = MQConfig.Feed_Dynamic_Publish_QUEUE)
    public void feedDynamicPublish(String message){
        PublishDynamic request=JsonUtil.stringToBean(message,PublishDynamic.class);
        int userId=request.getUserId(),dynamicId=request.getDynamicId();
        log.info("RabbitMQ消费了一条用户 {} 发布的动态 {} 的消息",userId,dynamicId);
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            Set<String> followMeSet=jedis.zrange(RedisKeys.followMe(userId),0,-1);
            long nowTs=System.currentTimeMillis();
            // 每个用户都有一个发feed收件箱
            jedis.zadd(RedisKeys.dynamicFeedSend(request.getUserId()),nowTs,String.valueOf(dynamicId));
            // 如果发布者是大V,则结束
            if (jedis.sismember(RedisKeys.bigV(),String.valueOf(userId))|| followMeSet.size()==0){
                return;
            }
            Pipeline pipe = jedis.pipelined();// 管道优化网络耗时
            // 给每个粉丝推送一条动态
            for(String str:followMeSet){
                pipe.zadd(RedisKeys.dynamicFeedReceive(Integer.valueOf(str)),nowTs,String.valueOf(dynamicId));
            }
            pipe.sync();
        }finally {
            CacheService.returnToPool(jedis);
        }

    }

    /**
     * 关注取关处理队列发现新消息时，取队首消息出队列。
     * 根据动作标识判断是关注还是取关操作。
     * 如果是关注，拉取关注者的发Feed有序集中的动态，将最近的动态ID写入用户自己的收Feed中。
     * 如果是取关，遍历用户自己的收Feed，剔除其中取关UID的动态记录。
     * 消息处理完成，检查队列是否还有消息，无则阻塞。
     * @param message
     */
    @RabbitListener(queues = MQConfig.Feed_Follow_QUEUE)
    public void feedFollow(String message) {
        FollowOrNot followOrNot = JsonUtil.stringToBean(message, FollowOrNot.class);
        int userId=followOrNot.getUserId(),toUserId=followOrNot.getToUserId();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipe = jedis.pipelined();// 管道优化网络耗时
            Set<Tuple> set=jedis.zrangeWithScores(RedisKeys.dynamicFeedSend(toUserId),0,-1);
            // 关注
            if (followOrNot.isFlag()) {
                log.info("RabbitMQ消费了一条用户 {} 关注用户 {} 的消息", userId, toUserId);
                // 将被关注者的发feed存到关注者的收feed
                for (Tuple tuple: set) {
                    pipe.zadd(RedisKeys.dynamicFeedReceive(userId),tuple.getScore(), tuple.getElement());
                }
            } else {// 取消关注
                log.info("RabbitMQ消费了一条用户 {} 取消关注用户 {} 的消息", userId, toUserId);
                // 将被关注者的发feed从关注者的收feed中删除
                for (Tuple tuple:set) {
                    pipe.zrem(RedisKeys.dynamicFeedReceive(followOrNot.getUserId()), tuple.getElement());
                }
            }
            pipe.sync();
        }finally {
            CacheService.returnToPool(jedis);
        }
    }

    @RabbitListener(queues = MQConfig.Comment_Notice)
    public void commentNotice(String message){

    }
}
