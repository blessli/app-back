package com.ldm.entity.dynamic;

import lombok.Data;

import java.util.List;
@Data
public class Dynamic {
    private int dynamicId;
    private String avatar;
    private String userId;
    private String userNickname;
    private String publishTime;
    private String dynamicType;
    private String content;
    private String images;
    private List<String> imageList;
    private String publishLocation;
    private int isLike;
    private int isFocus;
    private int likeCount;
    private int commentCount;
}
