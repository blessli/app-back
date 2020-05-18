package com.ldm.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author lidongming
 * @ClassName PublishDynamic.java
 * @Description 发表动态
 * @createTime 2020年04月04日 04:21:00
 */
@Data
public class PublishDynamic {
    private int dynamicId;// 动态ID
    @NotEmpty
    private String content;// 动态内容
    @NotEmpty
    private String images;// 图片列表
    @NotEmpty
    private String publishLocation;// 发布地点
    @Min(1)
    private int userId;// 用户ID
}
