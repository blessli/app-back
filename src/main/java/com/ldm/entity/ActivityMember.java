package com.ldm.entity;

import lombok.Data;

@Data
public class ActivityMember {
    private int activityId;// 用于activity_view初始化到redis
    private int userId;
    private String userNickname;
    private String publishTime;
    private String avatar;
}
