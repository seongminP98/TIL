package com.sm.lab.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setConnectionMinimumIdleSize(10)
                .setConnectionPoolSize(64)
                .setTimeout(5000)
                .setRetryInterval(1500)
                .setRetryAttempts(3);
        return Redisson.create(config);
    }
}
