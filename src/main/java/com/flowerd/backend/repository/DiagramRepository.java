package com.flowerd.backend.repository;

import com.flowerd.backend.entity.Diagram;
import com.flowerd.backend.entity.Project;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DiagramRepository extends ReactiveMongoRepository<Diagram, ObjectId> {
    Flux<Diagram> findAllByProjectId(ObjectId projectId);
}
