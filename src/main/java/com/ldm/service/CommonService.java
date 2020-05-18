package com.ldm.service;

import com.alibaba.fastjson.JSON;
import com.ldm.dao.ActivityDao;
import com.ldm.dao.DynamicDao;
import com.ldm.pojo.MsgSecCheck;
import com.ldm.util.DateHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;

/**
 * @author lidongming
 * @ClassName CommonService.java
 * @Description 公共服务
 * @createTime 2020年04月08日 23:38:00
 */
@Service
public class CommonService {

    @Autowired
    private ActivityDao activityDao;
    /**
     * @title 检查一段文本是否含有违法违规内容。
     * @description
     * @author lidongming
     * @updateTime 2020/4/8 23:48
     */
    public MsgSecCheck msgSecCheck(String access_token,String content) throws IOException {
        String url = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token="+access_token;
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        //添加请求头
        con.setRequestMethod("POST");
        //发送Post请求
        con.setDoOutput(true);
        con.setDoInput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        HashMap map=new HashMap();
        map.put("content","祖国是我的骄傲");
        System.out.println(JSON.toJSONString(map));
        wr.writeBytes(JSON.toJSONString(map));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        MsgSecCheck msgSecCheck=JSON.parseObject(response.toString(),MsgSecCheck.class);
        System.out.println(msgSecCheck);
        return msgSecCheck;
    }



}
