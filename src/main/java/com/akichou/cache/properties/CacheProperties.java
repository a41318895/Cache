package com.akichou.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cache")
@Data
public class CacheProperties {

    private int maxSize = 1000 ;

    private int expirationMinutes = 10 ;

    private int randomRate = 5 ;

    private int redisTimeout = 30 ;

    private int redisNullTimeout = 10 ;

    private int statusPrintDelayInit = 1 ;

    private int statusPrintPeriod = 1 ;
}
