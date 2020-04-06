package com.ldm.entity;

import lombok.Data;

import java.util.List;
@Data
public class ActivityDetail {
    private Activity activity;
    /**
     * 当前用户是否已加入该活动
     * 0为1已加入，1为未申请，2为已申请
     */
    private int isJoined;
    private List<Comment> activityCommentList;
}
