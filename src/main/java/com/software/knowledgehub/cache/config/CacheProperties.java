package com.software.knowledgehub.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private Duration detailTtl;
}
