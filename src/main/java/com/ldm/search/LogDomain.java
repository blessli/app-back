//package com.ldm.search;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//
//@Data
//@Document(indexName = "sys-log")
//public class LogDomain {
//    @Id
//    private int userId;
//    private String logType;
//    @Field(analyzer = "ik_max_word")
//    private String title;
//    @Field(analyzer = "ik_max_word")
//    private String url;
//    private String method;
//    private Object params;
//    @Field(analyzer = "ik_max_word")
//    private String exception;
//    private String beginTime;
//    private String endTime;
//}