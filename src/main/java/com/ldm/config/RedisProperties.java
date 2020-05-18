package com.ldm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.cache")
public class RedisProperties {
    private int expireSeconds;
    private String clusterNodes;
    private int commandTimeout;
}
