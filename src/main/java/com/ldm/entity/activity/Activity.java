package com.ldm.entity.activity;

import lombok.Data;

import java.util.List;

@Data
public class Activity {
    private int activityId;
    private String activityName;
    private String userId;
    private String avatar;
    private String activityType;
    private String locationName;
    private double longitude;
    private double latitude;
    private String beginTime;
    private String endTime;
    private String genderLimit;
    private int totalCount;
    private String remark;
    private int lookCount;
    private String images;
    private String publishTime;
    private int commentCount;
    private String userNickname;
    private List<String> imageList;
}
