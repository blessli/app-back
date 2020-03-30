package com.ldm.entity;

import lombok.Data;

@Data
public class DynamicSearch {
    private int dynamicId;
    private String userId;
    private String avatar;
    private String userNickname;
    private String content;
    private String images;
    private String location;
    private String publishTime;
    private int likeCount;
    private int commentCount;
    private String activityType;
    private int isFocus;
}
