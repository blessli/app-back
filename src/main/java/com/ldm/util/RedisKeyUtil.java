package com.ldm.util;

public class RedisKeyUtil {
    /**
     * @title 获取用户个人信息:avatar,userNickname
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/10 1:51 
     */
    public static String getUserInfo(int userId){
        return "userInfo:"+userId;
    }

    public static String getToken(int userId){
        return "token:"+userId;
    }

    /**
     * @title 获取活动信息:userId,image
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/11 17:12 
     */
    public static String getActivityInfo(int activityId){
        return "activityInfo:"+activityId;
    }
    
    /**
     * @title 获取动态信息:userId,image
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/11 17:16
     */
    public static String getDynamicInfo(int dynamicId){
        return "dynamInfo:"+dynamicId;
    }

    public static String getCommentNotice(int userId){
        return "comment:notice:"+userId;
    }

    /**
     * @title 获取消息页的四个通知的未读数
     * @description flag为0:申请通知,flag为1:点赞,flag为2:评论,flag为3:关注
     * @author lidongming
     * @updateTime 2020/4/11 15:51
     */
    public static String getCommentNoticeUnread(int flag,int userId){
        return "notice:unread:"+flag+":"+userId;
    }

    public static String getDynamicLike(int dynamicId){
        return "dynamic:like:"+dynamicId;
    }
    public static String getChatRecord(int userId,int toUserId){
        return "chat:record:"+userId+":"+toUserId;
    }
    public static String limitFrequency(String type,int userId){
        return "limit:frequency:"+type+":"+userId;
    }
    public static String followMe(int userId){
        return "followMe:"+userId;
    }
    public static String meFollow(int userId){
        return "meFollow:"+userId;
    }
}
