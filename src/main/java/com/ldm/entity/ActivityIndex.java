package com.ldm.entity;

import lombok.Data;

import java.util.List;
/**
 * @title 活动主页
 * @description
 * @author lidongming
 * @updateTime 2020/4/17 19:09
 */
@Data
public class ActivityIndex {
    private int activityId;// 活动ID
    private String activityName;// 活动名称
    private int userId;// 用户ID
    private String avatar;// 头像地址
    private String activityType;// 活动类型
    private String locationName;// 活动地址
    private String beginTime;// 活动开始时间
    private String endTime;// 活动结束时间
    private int viewCount;// 浏览量
    private int commentCount;// 评论量
    private String images;// 图片列表，数据库中以逗号分开
    private String publishTime;// 发表时间
    private String userNickname;// 用户昵称
    private List<String> imageList;// 返回给前端的图片列表是一个list
    private int activityStatus;
    private Boolean isViewed;// 用户是否浏览过该活动
}
