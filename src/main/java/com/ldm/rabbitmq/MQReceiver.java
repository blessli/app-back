package com.ldm.rabbitmq;

//@Service
//public class MQReceiver {
//    @Autowired
//    private CacheService cacheService;
//    @Autowired
//    private AppPush appPush;
//    /**
//     * 阿里云发送短信验证码，redis保存验证码，并设置有效期5分钟
//     * @param phone
//     */
//    @RabbitListener(queues = MQConfig.SMS_QUEUE)
//    public void receive(String phone){
//        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4Fwe6hHrbvykruL6WyMF", "WCcnIqRIKYb2dC8IAmu55tyRxzY7bh");
//        IAcsClient client = new DefaultAcsClient(profile);
//        String code= RandomUtil.generateDigitalStr(6);
//        CommonRequest request = new CommonRequest();
//        request.setMethod(MethodType.POST);
//        request.setDomain("dysmsapi.aliyuncs.com");
//        request.setVersion("2017-05-25");
//        request.setAction("SendSms");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//        request.putQueryParameter("PhoneNumbers", phone);
//        request.putQueryParameter("SignName", "友约");
//        request.putQueryParameter("TemplateCode", "SMS_184631762");
//        String key="{\"code\":"+code+"}";
//        request.putQueryParameter("TemplateParam",key);
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            System.out.println(response.getData());
//            cacheService.set("token:code:"+phone,code,"NX","EX",300);
//        } catch (ServerException e) {
//            e.printStackTrace();
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 使用个推SDK推送消息
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.FEED_QUEUE)
//    public void handleFeed(String message){
//        appPush.push(message,message);
//    }
//}
