package com.flowerd.backend.repository;

import com.flowerd.backend.entity.Table;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TableRepository extends ReactiveMongoRepository<Table, ObjectId> {
    Flux<Table> findAllBySchemaId(ObjectId schemaId);
}
