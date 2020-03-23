package com.ldm.entity.activity;

import lombok.Data;

@Data
public class ActivityComment {
    private int commentId;
    private String userId;
    private String userNickname;
    private int commentReplyCount;
    private String content;
    private String commentTime;
    private int activityId;
    private String avatar;
}
