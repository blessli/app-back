package com.ldm;


import com.ldm.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackApplicationTests.class)
public class BackApplicationTests {
    @Autowired
    UserDao userDao;
    @Test
    public void test(){
        userDao.insertTag(123,"lidongming");
    }
}
