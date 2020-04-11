package com.ldm.rabbitmq;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.ldm.entity.CommentNotice;
import com.ldm.service.CacheService;
import com.ldm.util.JsonUtil;
import com.ldm.util.RandomUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    @Autowired
    private CacheService cacheService;
    /**
     * 阿里云发送短信验证码，redis保存验证码，并设置有效期5分钟
     * @param phone
     */
    @RabbitListener(queues = MQConfig.SMS_QUEUE)
    public void receive(String phone){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4Fwe6hHrbvykruL6WyMF", "WCcnIqRIKYb2dC8IAmu55tyRxzY7bh");
        IAcsClient client = new DefaultAcsClient(profile);
        String code= RandomUtil.generateDigitalStr(6);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "友约");
        request.putQueryParameter("TemplateCode", "SMS_184631762");
        String key="{\"code\":"+code+"}";
        request.putQueryParameter("TemplateParam",key);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态发布处理队列发现新消息时，取队首消息出队列。
     * 根据消息中的发布者UID，遍历其粉丝列表(当以后全站粉丝量较大时，可扩展为选择性推送)。
     * 给每个粉丝推送一条动态，将动态ID和时间戳写入粉丝的收Feed有序集中。
     * 消息处理完成，检查队列是否还有消息，无则阻塞。
     * @param message
     */
    @RabbitListener(queues = MQConfig.Feed_Dynamic_Publish_QUEUE)
    public void feedDynamicPublish(String message){

    }

    /**
     * 关注取关处理队列发现新消息时，取队首消息出队列。
     * 根据动作标识判断是关注还是取关操作。
     * 如果是关注，拉取关注者的发Feed有序集中的动态，将最近的动态ID写入用户自己的收Feed中。
     * 如果是取关，遍历用户自己的收Feed，剔除其中取关UID的动态记录。
     * 消息处理完成，检查队列是否还有消息，无则阻塞。
     * @param message
     */
    @RabbitListener(queues = MQConfig.Feed_Dynamic_Publish_QUEUE)
    public void feedFollow(String message){

    }

    @RabbitListener(queues = MQConfig.Comment_Notice)
    public void commentNotice(String message){
        CommentNotice commentNotice= JsonUtil.stringToBean(message,CommentNotice.class);

    }
}
