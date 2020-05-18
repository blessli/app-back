package com.ldm.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author lidongming
 * @ClassName PublishActivity.java
 * @Description 发表活动
 * @createTime 2020年04月04日 04:21:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishActivity {
    private int activityId;// 活动ID
    @NotEmpty
    private String activityName;// 活动名
    @Min(1)
    private int userId;// 用户ID
    @NotEmpty
    private String activityType;// 活动类型
    @NotEmpty
    private String locationName;// 活动地点
    private double longitude;// 经度
    private double latitude;// 纬度
    @NotEmpty
    private String beginTime;// 开始时间
    @NotEmpty
    private String endTime;// 结束时间
    @NotEmpty
    private String require;// 要求
    @NotEmpty
    private String remark;// 备注
    @NotEmpty
    private String images;// 图片列表
}
