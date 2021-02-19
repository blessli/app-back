package com.ldm.entity;

import lombok.Data;

import java.util.List;
@Data
public class ActivityDetail {
    private int activityId;// 活动ID
    private String activityName;// 活动名称
    private int userId;// 用户ID
    private String avatar;// 头像地址
    private String activityType;// 活动类型
    private String locationName;// 活动地址
    private double longitude;// 经度
    private double latitude;// 纬度
    private String beginTime;// 活动开始时间
    private String endTime;// 活动结束时间
    private String require;// 要求
    private String remark;// 备注
    private int viewCount;// 浏览量
    private int commentCount;// 评论量
    private Long memberCount;// 已加入人数
    private String images;// 图片列表，数据库中以逗号分开
    private String publishTime;// 发表时间
    private String userNickname;// 用户昵称
    private List<String> imageList;// 返回给前端的图片列表是一个list
    /**
     *
     * 当前用户是否已加入该活动
     * 0为1已加入，1为未申请，2为已申请
     */
    private Boolean isJoined;
    private List<Comment> activityCommentList;
    private int status;
}
