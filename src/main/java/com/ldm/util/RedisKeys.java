package com.ldm.util;

public class RedisKeys {

    // online socket
    public static String online(int userId, String pageSign) {
        return "online:userId:" + userId + ":pageSign:" + pageSign;
    }
    /**
     * @title 获取用户个人信息:avatar,userNickname,openId
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/10 1:51 
     */
    public static String userInfo(int userId){
        return "userInfo:"+userId;
    }

    /**
     * @title redis存储token,记录登录状态
     * @description
     * @author lidongming
     * @updateTime 2020/4/15 14:00
     */
    public static String token(String openId){
        return "token:"+openId;
    }

    public static String firstToken(String openId){
        return "token:first:"+openId;
    }

    /**
     * @title 获取活动基本信息
     * @description redis存储userId,image,viewCount,commentCount,shareCount,publishTime
     * @author lidongming 
     * @updateTime 2020/4/11 17:12 
     */
    public static String activityInfo(int activityId){
        return "activityInfo:"+activityId;
    }
    
    /**
     * @title 获取动态基本信息
     * @description redis存储userId,image
     * @author lidongming 
     * @updateTime 2020/4/11 17:16
     */
    public static String dynamicInfo(int dynamicId){
        return "dynamicInfo:"+dynamicId;
    }

    /**
     * @title 获取消息页的四个通知的未读数
     * @description flag为0:申请通知,flag为1:点赞,flag为2:评论,flag为3:关注
     * @author lidongming
     * @updateTime 2020/4/11 15:51
     */
    public static String noticeUnread(int flag,int userId){
        return "notice:unread:"+flag+":"+userId;
    }

    /**
     * @title 点赞动态的用户列表
     * @description 使用set存储给该动态点赞了的userId
     * @author lidongming
     * @updateTime 2020/4/14 21:49
     */
    public static String likeDynamic(int dynamicId){
        return "dynamic:like:"+dynamicId;
    }

    /**
     * @title 限制频率,用于控制用户行为
     * @description 滑动窗口
     * @author lidongming
     * @updateTime 2020/4/14 21:50
     */
    public static String limitFrequency(String type,int userId){
        return "limit:frequency:"+type+":"+userId;
    }

    /**
     * @title 关注我的用户列表
     * @description 使用set存储userId
     * @author lidongming
     * @updateTime 2020/4/13 23:47
     */
    public static String followMe(int userId){
        return "followMe:"+userId;
    }
    /**
     * @title 我关注的用户列表
     * @description 使用set存储userId
     * @author lidongming
     * @updateTime 2020/4/13 23:46
     */
    public static String meFollow(int userId){
        return "meFollow:"+userId;
    }

    /**
     * @title 用户的收Feed
     * @description 使用zset存储dynamicId
     * @author lidongming
     * @updateTime 2020/4/13 23:50
     */
    public static String dynamicFeedReceive(int userId){
        return "dynamic:feed:receive:"+userId;
    }
    /**
     * @title 用户的收Feed
     * @description 使用set存储dynamicId
     * @author lidongming
     * @updateTime 2020/4/14 0:37
     */
    public static String dynamicFeedSend(int userId){
        return "dynamic:feed:send:"+userId;
    }

    /**
     * @title 删除了的动态集合
     * @description 使用set存储,用于feed流
     * @author lidongming
     * @updateTime 2020/4/14 21:45
     */
    public static String deletedDynamic(){
        return "dynamic:deleted";
    }
    /**
     * @title 判断某用户是否浏览过该活动
     * @description 使用set存储userId
     * @author lidongming
     * @updateTime 2020/4/14 21:44
     */
    public static String activityViewed(int activityId){
        return "activity:viewed:"+activityId;
    }

    /**
     * @title 判断某用户是否加入了该活动
     * @description 使用set存储userId
     * @author lidongming
     * @updateTime 2020/4/14 21:53
     */
    public static String activityJoined(int activityId){
        return "activity:joined:"+activityId;
    }


    /**
     * @title 大V集合
     * @description 用于feed流
     * @author lidongming
     * @updateTime 2020/4/16 16:58
     */
    public static String bigV(){
        return "dynamic:bigV";
    }
}
