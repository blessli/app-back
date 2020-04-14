package com.ldm.util;

public class RedisKeys {
    /**
     * @title 获取用户个人信息:avatar,userNickname
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/10 1:51 
     */
    public static String userInfo(int userId){
        return "userInfo:"+userId;
    }

    public static String token(int userId){
        return "token:"+userId;
    }

    /**
     * @title 获取活动信息:userId,image
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/11 17:12 
     */
    public static String activityInfo(int activityId){
        return "activityInfo:"+activityId;
    }
    
    /**
     * @title 获取动态信息:userId,image
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/11 17:16
     */
    public static String dynamicInfo(int dynamicId){
        return "dynamInfo:"+dynamicId;
    }

    public static String commentNotice(int userId){
        return "comment:notice:"+userId;
    }

    /**
     * @title 获取消息页的四个通知的未读数
     * @description flag为0:申请通知,flag为1:点赞,flag为2:评论,flag为3:关注
     * @author lidongming
     * @updateTime 2020/4/11 15:51
     */
    public static String commentNoticeUnread(int flag,int userId){
        return "notice:unread:"+flag+":"+userId;
    }

    public static String likeDynamic(int dynamicId){
        return "dynamic:like:"+dynamicId;
    }
    public static String chatRecord(int userId,int toUserId){
        return "chat:record:"+userId+":"+toUserId;
    }
    public static String limitFrequency(String type,int userId){
        return "limit:frequency:"+type+":"+userId;
    }

    /**
     * @title 关注我的用户列表
     * @description 使用集合存储userId
     * @author lidongming
     * @updateTime 2020/4/13 23:47
     */
    public static String followMe(int userId){
        return "followMe:"+userId;
    }
    /**
     * @title
     * @description
     * @author lidongming
     * @updateTime 2020/4/13 23:46
     */
    public static String meFollow(int userId){
        return "meFollow:"+userId;
    }
    public static String getAllActivity(int activityId){
        return "activity:"+activityId;
    }
    /**
     * @title 该用户是否浏览过该活动
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/13 21:41
     */
    public static String activityIsView(int activityId,int userId){
        return "activity:isView:"+activityId+":"+userId;
    }
    /**
     * @title 用户的收Feed
     * @description 使用zset存储dynamicId
     * @author lidongming
     * @updateTime 2020/4/13 23:50
     */
    public static String dynamicFeedReceive(int userId){
        return "feed:dynamic:receive:"+userId;
    }
    /**
     * @title 用户的收Feed
     * @description 使用zset存储dynamicId
     * @author lidongming
     * @updateTime 2020/4/14 0:37
     */
    public static String dynamicFeedSend(int userId){
        return "feed:dynamic:send:"+userId;
    }

    public static String allDynamic(int dynamicId){
        return "dynamic:"+dynamicId;
    }

    public static String dynamicFeedId(int dynamicId){
        return "feed:single:dynamic:"+dynamicId;
    }
    public static String deletedDynamic(){
        return "deleted:dynamic";
    }
}
