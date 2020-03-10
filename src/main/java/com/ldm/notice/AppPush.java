package com.ldm.notice;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 个推SDK实现消息推送
 */
@Service
public class AppPush {

    // STEP1：获取应用基本信息
    private static String appId = "";
    private static String appKey = "";
    private static String masterSecret = "";
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";
    public void push(String title,String text) {
        IGtPush push = new IGtPush(url, appKey, masterSecret);

        Style0 style = new Style0();
        // STEP2：设置推送标题、推送内容
        style.setTitle(title);
        style.setText(text);
        style.setLogo("push.png");  // 设置推送图标
        // STEP3：设置响铃、震动等推送效果
        style.setRing(true);  // 设置响铃
        style.setVibrate(true);  // 设置震动


        // STEP4：选择通知模板
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setStyle(style);


        // STEP5：定义"AppMessage"类型消息对象,设置推送消息有效期等推送参数
        List<String> appIds = new ArrayList<String>();
        appIds.add(appId);
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);  // 时间单位为毫秒

        // STEP6：执行推送
        IPushResult ret = push.pushMessageToApp(message);
        System.out.println(ret.getResponse().toString());
    }
}
