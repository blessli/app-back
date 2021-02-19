package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName ActivityApply.java
 * @Description TODO
 * @createTime 2020年04月12日 00:22:00
 */
@Data
public class ApplyNotice {
    private int userId;
    private String avatar;// redis中get
    private String userNickname;// redis中get
    private String publishTime;
    private String activityName;
    private int activityId;
    private int status;// 0：申请中，1：拒绝，2：同意
}
