package com.flowerd.backend.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@Slf4j
public class MailCertRedisRepository {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public MailCertRedisRepository(@Qualifier("reactiveRedisTemplate2") ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> save(String email, String uuid) {
        return reactiveRedisTemplate.opsForValue()
                .set(email, uuid, Duration.ofSeconds(600))
                .doOnSuccess(result -> log.info("Saved email {} with uuid {}", email, uuid))
                .doOnError(error -> log.error("Error saving email {} with uuid {}", email, uuid, error)); // 600초(10분) TTL 설정
    }

    public Mono<String> findByEmail(String email) {
        return reactiveRedisTemplate.opsForValue()
                .get(email)
                .doOnSuccess(result -> log.info("Found email {}: {}", email, result))
                .doOnError(error -> log.error("Error finding email {}", email, error));
    }

    public Mono<Boolean> update(String email, String uuid) {
        return reactiveRedisTemplate.opsForValue()
                .set(email, uuid, Duration.ofSeconds(600))
                .doOnSuccess(result -> log.info("Updated email {} with uuid {}", email, uuid))
                .doOnError(error -> log.error("Error updating email {} with uuid {}", email, uuid, error));
    }

    public Mono<Boolean> delete(String email) {
        return reactiveRedisTemplate.opsForValue()
                .delete(email)
                .doOnSuccess(result -> log.info("Deleted email {}", email))
                .doOnError(error -> log.error("Error deleting email {}", email, error));
    }
}
