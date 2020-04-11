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
     * @title 获取活动信息:userId
     * @description 
     * @author lidongming 
     * @updateTime 2020/4/11 17:12 
     */
    public static String getActivityInfo(int activityId){
        return "activityInfo:"+activityId;
    }
    
    /**
     * @title 获取动态信息:userId
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
        return "comment:notice:unRead:"+flag+":"+userId;
    }
}
