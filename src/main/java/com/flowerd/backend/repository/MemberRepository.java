package com.flowerd.backend.repository;

import com.flowerd.backend.entity.Member;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface MemberRepository extends ReactiveMongoRepository<Member, ObjectId> {
    Mono<Boolean> existsByEmail(String email);

    Mono<Member> findByEmail(String email);
}
