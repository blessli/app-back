package com.ldm.request;

import lombok.Data;


@Data
public class PublishActivityRequest {
    private String activityName;
    private String userId;
    private String activityType;
    private String locationName;
    private double longitude;
    private double latitude;
    private String beginTime;
    private String endTime;
    private String genderLimit;
    private int totalCount;
    private String remark;
    private String images;
    private String publishTime;
}
