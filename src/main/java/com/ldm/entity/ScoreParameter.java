package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName ScoreParameter.java
 * @Description 活动分数的四个维度
 * @createTime 2020年04月21日 11:16:00
 */
@Data
public class ScoreParameter {
    private String publishTime;
    private int commentCount;
    private int shareCount;
    private int viewCount;
}
