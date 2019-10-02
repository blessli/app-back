package com.ldm.entity.activity;

import lombok.Data;

import java.util.List;
@Data
public class ActivityDetail {
    private Activity activity;
    private List<ActivityMember> activityMemberList;
    private List<ActivityComment> activityCommentList;
}
