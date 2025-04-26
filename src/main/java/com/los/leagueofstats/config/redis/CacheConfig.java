package com.los.leagueofstats.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    public static final String PROFILE_CACHE_NAME = "profile";
    public static final String SEASON_MATCHES_CACHE_NAME = "seasonMatchIds";
    public static final String GLOBAL_MATCHES_CACHE_NAME = "globalMatchIds";
    public static final String MATCH_CACHE_NAME = "match";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(PROFILE_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
        configs.put(SEASON_MATCHES_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(25)));
        configs.put(GLOBAL_MATCHES_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(5)));
        configs.put(MATCH_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(3)));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
