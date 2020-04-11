package com.ldm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author lidongming
 * @ClassName SearchResponse.java
 * @Description TODO
 * @createTime 2020年04月10日 18:57:00
 */
@Data
public class SearchResult {
    private Integer activityId;
    private Integer userId;
    private String activityName;
    private String activityType;
    private String locationName;
    private String publishTime;
    private String beginTime;
    private String endTime;
    private Integer viewCount;
    private Integer commentCount;
    private String userNickname;
    private String avatar;
    private List<String> imageList;
}
