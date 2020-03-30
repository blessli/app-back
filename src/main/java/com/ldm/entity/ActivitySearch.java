package com.ldm.entity;

import lombok.Data;

@Data
public class ActivitySearch {
    private int activityId;
    private String userId;
    private String publishTime;
    private String userNickname;
    private String avatar;
    private String activityName;
    private String activityType;
    private String beginTime;
    private String endTime;
    private String location;
    private int lookCount;
    private int commentCount;
    private String images;
}
