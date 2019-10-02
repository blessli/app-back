package com.ldm.request;

import lombok.Data;

@Data
public class EditUserInfoRequest {
    private String userName;
    private String userNickname;
    private String gender;
    private String birthday;
    private String location;
    private String signature;
}
