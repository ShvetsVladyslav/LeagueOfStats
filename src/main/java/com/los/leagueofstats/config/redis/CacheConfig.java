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

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put("summoner", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)));
        configs.put("matchIds", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
        configs.put("match", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
