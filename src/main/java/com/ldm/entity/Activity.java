package com.ldm.entity;

import lombok.Data;

import java.util.List;

@Data
public class Activity {
    private int activityId;// 活动ID
    private String activityName;// 活动名称
    private String userId;// 用户ID
    private String avatar;// 头像地址
    private String activityType;// 活动类型
    private String locationName;// 活动地址
    private double longitude;// 经度
    private double latitude;// 纬度
    private String beginTime;// 活动开始时间
    private String endTime;// 活动结束时间
    private String genderLimit;// 性别限制
    private int totalCount;// 活动可加入名额
    private String remark;// 备注
    private int activityViewCount;// 浏览量
    private int activityCommentCount;// 评论量
    private int activityMemberCount;// 已加入人数
    private String images;// 图片地址
    private String publishTime;// 发表时间
    private String userNickname;// 用户昵称
    private List<String> imageList;
}
