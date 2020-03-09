package com.ldm;


import com.ldm.service.activity.ActivityService;
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
        System.out.println("hello");
    }

}
