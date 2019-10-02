package com.ldm;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Iterator;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackApplicationTests.class)
public class BackApplicationTests {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Test
    public void contextLoads() {
        long now=System.currentTimeMillis();
        System.out.println(now);
        redisTemplate.opsForZSet().add("activity:search:123456","mybatis",0.0);
        redisTemplate.opsForZSet().add("activity:search:123456","spring",Double.valueOf(1569293782783.0));
        redisTemplate.opsForZSet().removeRange("activity:search:123456",0,0);
        redisTemplate.opsForZSet().incrementScore("activity:search:123456","mybatis",1.0);
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet=redisTemplate.opsForZSet().reverseRangeWithScores("activity:search:123456",0,2);
        Iterator iterator=typedTupleSet.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> typedTuple = (ZSetOperations.TypedTuple<Object>) iterator.next();
            System.out.println(typedTuple.getValue());
        }
        System.out.println("hello world!!!");
    }
    

}
