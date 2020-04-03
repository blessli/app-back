package com.ldm.request;

import lombok.Data;
/**
 * @author lidongming
 * @ClassName PublishDynamic.java
 * @Description 发表动态
 * @createTime 2020年04月04日 04:21:00
 */
@Data
public class PublishDynamic {
    private String content;// 动态内容
    private String images;// 图片列表
    private String publishTime;// 发表时间
    private String publishLocation;// 发布地点
    private int userId;// 用户ID
}
