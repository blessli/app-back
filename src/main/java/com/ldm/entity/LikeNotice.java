package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName LikeNotice.java
 * @Description TODO
 * @createTime 2020年04月11日 13:11:00
 */
@Data
public class LikeNotice {
    private int dynamicId;
    private int userId;
    private String publishTime;
    private String avatar;
    private String image;
    private String userNickname;
}
