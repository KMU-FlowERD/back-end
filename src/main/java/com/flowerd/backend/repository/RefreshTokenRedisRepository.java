package com.flowerd.backend.repository;

import com.flowerd.backend.entity.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@Slf4j
public class RefreshTokenRedisRepository {

    private final ReactiveRedisTemplate<String, RefreshToken> reactiveRedisTemplate;

    public RefreshTokenRedisRepository(@Qualifier("reactiveRedisTemplate1")
                                       ReactiveRedisTemplate<String, RefreshToken> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    // 시간제한 10분
    public Mono<Boolean> save(RefreshToken refreshToken) {
        return reactiveRedisTemplate.opsForValue()
                .set(refreshToken.getEmail(), refreshToken, Duration.ofSeconds(600))
                .doOnSuccess(result -> log.info("Successfully saved refresh token for email: {}", refreshToken.getEmail()))
                .doOnError(error -> log.error("Error saving refresh token for email: {}. Error: {}", refreshToken.getEmail(), error.getMessage()));
    }

    public Mono<RefreshToken> findById(String email) {
        return reactiveRedisTemplate.opsForValue()
                .get(email)
                .doOnSuccess(result -> {
                    if (result != null) {
                        log.info("Successfully found refresh token for email: {}", email);
                    } else {
                        log.info("No refresh token found for email: {}", email);
                    }
                })
                .doOnError(error -> log.error("Error finding refresh token for email: {}. Error: {}", email, error.getMessage()));
    }

    public Mono<Boolean> delete(RefreshToken refreshToken) {
        return reactiveRedisTemplate.opsForValue()
                .delete(refreshToken.getEmail())
                .doOnSuccess(result -> log.info("Successfully deleted refresh token for email: {}", refreshToken.getEmail()))
                .doOnError(error -> log.error("Error deleting refresh token for email: {}. Error: {}", refreshToken.getEmail(), error.getMessage()));
    }
}
