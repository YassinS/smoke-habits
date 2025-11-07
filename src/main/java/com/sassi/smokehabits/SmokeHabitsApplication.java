package com.sassi.smokehabits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.sassi.smokehabits.repository")
public class SmokeHabitsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmokeHabitsApplication.class, args);
    }

}
