package com.flowerd.backend.repository;

import com.flowerd.backend.entity.Schema;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SchemaRepository extends ReactiveMongoRepository<Schema, ObjectId> {
    Flux<Schema> findAllByProjectId(ObjectId projectId);
}
