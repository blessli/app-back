package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName EsActivity.java
 * @Description TODO
 * @createTime 2020年04月21日 13:30:00
 */
@Data
public class EsActivity {
    private int activityId;// 活动ID
    private String activityName;// 活动名称
    private String activityType;// 活动类型
    private String locationName;// 活动地址
    private String userNickname;// 用户昵称
}
