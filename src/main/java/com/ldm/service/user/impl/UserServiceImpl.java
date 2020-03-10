package com.ldm.service.user.impl;

import com.ldm.dao.UserDao;
import com.ldm.entity.user.UserInfo;
import com.ldm.rabbitmq.MQSender;
import com.ldm.service.cache.CacheService;
import com.ldm.service.user.UserService;
import com.ldm.util.MD5Util;
import com.ldm.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service(value = "/userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private MQSender mqSender;
    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;
    @Override
    public UserInfo selectUserCenter(int userId) {
        return null;
    }

    @Override
    public boolean register(String phone) {
        mqSender.sendSMS(phone);
        return true;
    }

    @Override
    public boolean registerWithCode(String phone,String code, String password) {
        String key="token:code:"+phone;
        String value=cacheService.get(key,String.class);
        if(value.equals(code)){// 偷懒，理想化
            String salt= RandomUtil.generateLetterStr(8);
            // 两次MD5：储存在数据库中的密码的md5 = MD5(MD5(input_password) + SALT) + db_salt
            if(userDao.register(phone,salt, MD5Util.MD5(password)+salt)>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public String login(String phone, String password) {
        return null;
    }

    @Override
    public boolean logout(String token) {
        return cacheService.delete(token);
    }

    @Override
    public boolean followActivityOrUser(int followerId, int followingId, int followType) {
        return false;
    }

    @Override
    public List<Integer> selectFollowedUserList(int userId) {
        return null;
    }

    @Override
    public List<String> selectActivityTypeList(int userId) {
        return null;
    }

    @Override
    public List<Integer> selectFanUserList(int userId) {
        return null;
    }
}
