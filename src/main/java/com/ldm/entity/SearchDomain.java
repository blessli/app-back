package com.ldm.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
@Data
@Document(indexName = "app",type = "activity")
public class SearchDomain {
    @Id
    private Integer activityId;
    @Field(analyzer = "ik_max_word")
    private String activityName;
    @Field(analyzer = "ik_max_word")
    private String activityType;
    @Field(analyzer = "ik_max_word")
    private String locationName;
    @Field(analyzer = "ik_max_word")
    private String userNickname;
}
