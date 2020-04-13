package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName ActivityApply.java
 * @Description TODO
 * @createTime 2020年04月12日 00:22:00
 */
@Data
public class ActivityApply {
    private int userId;
    private String avatar;
    private String userNickname;
    private String publishTime;
    private String activityName;
    private int status;
}
