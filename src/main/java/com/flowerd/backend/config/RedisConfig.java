package com.flowerd.backend.config;

import com.flowerd.backend.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${redis1.host}")
    public String REDIS_HOST1;
    @Value("${redis2.host}")
    public String REDIS_HOST2;
    @Value("${redis1.port}")
    public int REDIS_PORT1;
    @Value("${redis2.port}")
    public int REDIS_PORT2;

    // Reactive Redis 연결 설정
    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory1() {
        return new LettuceConnectionFactory(REDIS_HOST1, REDIS_PORT1);
    }

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory2() {
        return new LettuceConnectionFactory(REDIS_HOST2, REDIS_PORT2);
    }

    // Reactive Redis 데이터 템플릿 설정, Refresh 토큰 저장용으로 String : Object(RefreshToken) 형식으로 설정
    @Bean(name = "reactiveRedisTemplate1")
    public ReactiveRedisTemplate<String, RefreshToken> reactiveRedisTemplate1() {
        Jackson2JsonRedisSerializer<RefreshToken> serializer = new Jackson2JsonRedisSerializer<>(RefreshToken.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, RefreshToken> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, RefreshToken> context = builder
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory1(), context);
    }

    @Bean(name = "reactiveRedisTemplate2")
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate2() {
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, String> context = builder
                .value(new StringRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory2(), context);
    }
}
