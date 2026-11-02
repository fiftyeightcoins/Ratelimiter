package com.showchase.myRateLimiter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisConfig {

    @Value("${redis.host:redis-................oud.redislabs.com}")
    private String redisHost;

    @Value("${redis.port:}")
    private int redisPort;

    @Value("${redis.user:default}")
    private String redisUser;

    @Value("${redis.password:}")
    private String redisPassword;

    @Bean(destroyMethod = "close")
    public UnifiedJedis unifiedJedis() {
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user(redisUser)
                .password(redisPassword)
                .build();

        return new UnifiedJedis(new HostAndPort(redisHost, redisPort), config);
    }
}

