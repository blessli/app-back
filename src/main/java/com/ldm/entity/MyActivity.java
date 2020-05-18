package com.ldm.entity;

import lombok.Data;


/**
 * @author lidongming
 * @ClassName MyActivity.java
 * @Description 申请加入的活动
 * @createTime 2020年04月10日 17:35:00
 */
@Data
public class MyActivity {
    private int activityId;// 活动ID
    private String activityName;// 活动名称
    private String locationName;// 活动地址
    private String beginTime;// 活动开始时间
    private String endTime;// 活动结束时间
    private int memberCount;// 已加入人数
    private String images;
    private String image;// 图片列表中的第一张图片
    private String publishTime;// 申请时间
    private int applyStatus;// 0：申请中，1：拒绝，2：同意
}
