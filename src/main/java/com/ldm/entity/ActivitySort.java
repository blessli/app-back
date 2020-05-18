package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName ActivitySort.java
 * @Description TODO
 * @createTime 2020年04月19日 21:39:00
 */
@Data
public class ActivitySort {
    private int activityId;
    private String activityName;
    private String beginTime;
    private String endTime;
    private String locationName;
    private String images;
    private String image;
}
