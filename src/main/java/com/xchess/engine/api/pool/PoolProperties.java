package com.xchess.engine.api.pool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pool.engine")
public class PoolProperties {
    private int minIdle;
    private int maxTotal;
    private int timeBetweenEvictionRunsInMs;
    private int evictableIdleDurationInMs;
}
