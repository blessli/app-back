package com.ldm.request;

import lombok.Data;

@Data
public class PublishDynamicRequest {
    private String content;
    private String dynamicType;
    private String images;
    private String publishTime;
    private String publishLocation;
    private String userId;
}
