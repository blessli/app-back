package com.ldm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author lidongming
 * @ClassName DynamicDetail.java
 * @Description TODO
 * @createTime 2020年04月10日 20:08:00
 */
@Data
public class DynamicDetail {
    private int dynamicId;// 动态ID
    private String avatar;// 头像
    private int userId;// 用户ID
    private String userNickname;// 用户昵称
    private String publishTime;// 发表时间
    private String content;// 动态内容
    private String images;// 图片列表，数据库中以逗号分开
    private List<String> imageList;// 返回给前端的图片列表是一个list
    private String publishLocation;// 发表地点
    private Boolean isLike;
    private Long likeCount;// 点赞量
    private int commentCount;// 评论量
    List<Comment> commentList;
    private int status;
}
