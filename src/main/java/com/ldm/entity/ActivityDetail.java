package com.ldm.entity;

import lombok.Data;

import java.util.List;
@Data
public class ActivityDetail {
    private Activity activity;
    private List<ActivityMember> activityMemberList;
    private List<Comment> activityCommentList;
}
