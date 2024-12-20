package com.flowerd.backend.repository;

import com.flowerd.backend.entity.Diagram;
import com.flowerd.backend.entity.DiagramTable;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DiagramTableRepository extends ReactiveMongoRepository<DiagramTable, ObjectId>{
    Flux<DiagramTable> findAllByDiagramId(ObjectId diagramId);

    Flux<DiagramTable> findAllByTableId(ObjectId tableId);

    Mono<DiagramTable> findByDiagramIdAndTableId(ObjectId diagramId, ObjectId tableId);
}
