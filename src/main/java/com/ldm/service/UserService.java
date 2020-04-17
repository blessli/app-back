package com.ldm.service;


import com.alibaba.fastjson.JSON;
import com.ldm.dao.UserDao;
import com.ldm.pojo.AccessToken;
import com.ldm.pojo.LoginCredential;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.rabbitmq.MQSender;
import com.ldm.request.UserInfo;
import com.ldm.pojo.FollowOrNot;
import com.ldm.response.FollowUserInfo;
import com.ldm.response.UserProfile;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lidongming
 * @ClassName UserService.java
 * @Description 用户服务
 * @createTime 2020年04月04日 05:05:00
 */
@Service
public class UserService{
    @Autowired
    private UserDao userDao;



    /**
     * 通过连接池对象可以获得对redis的连接
     */
    @Autowired
    JedisPool jedisPool;
    /**
     * @title 登录凭证校验
     * @description 通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程
     * @author lidongming
     * @updateTime 2020/4/8 22:57 
     */
    public LoginCredential loginCredentialVerification(String code) throws Exception {

        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=wxa2456aa6cbac869c&secret=6626b587b45f7a6d3e93988f89979fb8&js_code=" +code+
                "&grant_type=authorization_code";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        LoginCredential loginCredential=JSON.parseObject(response.toString(),LoginCredential.class);

        return loginCredential;
    }
    /**
     * @title 获取接口调用凭证
     * @description 获取小程序全局唯一后台接口调用凭据（access_token）。调用绝大多数后台接口时都需使用 access_token，开发者需要进行妥善保存。
     * @author lidongming
     * @updateTime 2020/4/8 23:05
     */
    public AccessToken getAccessToken() throws Exception {

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxa2456aa6cbac869c&secret=6626b587b45f7a6d3e93988f89979fb8";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        AccessToken accessToken= JSON.parseObject(response.toString(),AccessToken.class);
        System.out.println(accessToken);
        return accessToken;
    }
    public int addUserInfo(UserInfo userInfo){
        return 0;
    }


    /**
     * @title 个人主页
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/14 22:34 
     */
    public UserProfile getUserProfile(int userId){
        Jedis jedis=jedisPool.getResource();
        UserProfile userProfile=new UserProfile();
        userProfile.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
        userProfile.setUserNickname(jedis.hget(RedisKeys.userInfo(userId),"userNickname"));
        userProfile.setFanCount(jedis.scard(RedisKeys.followMe(userId)));
        userProfile.setFocusCount(jedis.scard(RedisKeys.meFollow(userId)));
        CacheService.returnToPool(jedis);
        return userProfile;
    }
    public List<SimpleUserInfo> selectSimpleUserInfo(){
        return userDao.selectSimpleUserInfo();
    }


}
