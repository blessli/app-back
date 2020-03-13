package com.ldm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: nanjunyu
 * @Description:读取redis配置信息并装载
 * @Date: Create in  2018/6/14 16:16
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.cache")
public class RedisProperties {
    private int expireSeconds;
    private String clusterNodes;
    private int commandTimeout;
}
