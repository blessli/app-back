package com.ldm.request;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName PublishActivity.java
 * @Description 发表活动
 * @createTime 2020年04月04日 04:21:00
 */
@Data
public class PublishActivity {
    private String activityName;// 活动名
    private int userId;// 用户ID
    private String activityType;// 活动类型
    private String locationName;// 活动地点
    private double longitude;// 经度
    private double latitude;// 纬度
    private String beginTime;// 开始时间
    private String endTime;// 结束时间
    private String require;// 要求
    private String remark;// 备注
    private String images;// 图片列表
    private String publishTime;// 发表时间
}
