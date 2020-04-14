package com.ldm.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ldm.dao.UserDao;
import com.ldm.entity.AccessToken;
import com.ldm.entity.LoginCredential;
import com.ldm.entity.SimpleUserInfo;
import com.ldm.request.UserInfo;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
     * @title 获取该用户关注的用户列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:12
     */
    public List<SimpleUserInfo> getFollowedUserList(int userId, int pageNum,int pageSize){
        return userDao.getFollowedUserList(userId);
    }

    /**
     * @title 获取关注该用户的用户列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 17:15
     */
    public List<SimpleUserInfo> getFollowMeUserList(int userId, int pageNum,int pageSize){
        return userDao.getFollowMeUserList(userId, pageSize * pageSize, pageSize);
    }
    public List<SimpleUserInfo> selectSimpleUserInfo(){
        return userDao.selectSimpleUserInfo();
    }
}
