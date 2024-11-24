package com.flowerd.backend.repository;

import com.flowerd.backend.entity.Constraints;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConstraintsRepository extends ReactiveMongoRepository<Constraints, ObjectId>{

    Flux<Constraints> findAllByParentTableId(ObjectId TableId);

    Flux<Constraints> findAllByChildTableId(ObjectId TableId);

    Mono<Void> deleteAllByParentColumnId(ObjectId columnId);

    Mono<Void> deleteAllByChildColumnId(ObjectId columnId);
}
