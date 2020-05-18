package com.ldm.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author lidongming
 * @ClassName ChatHistory.java
 * @Description TODO
 * @createTime 2020年04月20日 19:33:00
 */
@Data
public class ChatHistoryRequest {
    @Min(1)
    private int userId;
    @NotEmpty
    private String pageSign;
    @NotEmpty
    private String msgFlag;
}
