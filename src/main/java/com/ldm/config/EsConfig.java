//package com.ldm.config;
//
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
////import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.common.transport.*;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import java.net.InetAddress;
//
///**
// * 创建TransportClient的类，esTemplate方法避免TransportClient每次使用创建和释放
// */
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.ldm.dao")
//@Component
//public class EsConfig {
//
//    @Value("${spring.data.elasticsearch.cluster-name}")
//    private String clusterName;
//
//    private Client esClient;
//
//    @Bean
//    public Client client() {
//        TransportClient client = null;
//        try {
//            final Settings elasticsearchSettings = Settings.builder()
//                    .put("client.transport.sniff", true)
//                    .put("cluster.name", "elasticsearch_brew").build();
//            client = new PreBuiltTransportClient(elasticsearchSettings);
//            client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return client;
//    }
//
//    //避免TransportClient每次使用创建和释放
//    public Client esTemplate() {
//        if( StringUtils.isEmpty(esClient) || StringUtils.isEmpty(esClient.admin())) {
//            esClient = client();
//            return esClient;
//        }
//        return esClient;
//    }
//
//}