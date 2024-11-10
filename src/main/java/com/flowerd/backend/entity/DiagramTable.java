package com.flowerd.backend.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

@Document(collection = "diagram_table")
@NoArgsConstructor
@Getter
@Setter
public class DiagramTable {
    @Id
    private ObjectId id;

    private ObjectId diagramId;

    private ObjectId tableId;

    private Long size_x;

    private Long size_y;


    public DiagramTable(ObjectId diagramId, ObjectId tableId, Long size_x, Long size_y) {
        this.diagramId = diagramId;
        this.tableId = tableId;
        this.size_x = size_x;
        this.size_y = size_y;
    }

    public static Mono<DiagramTable> of(ObjectId diagramId, ObjectId tableId, Long size_x, Long size_y) {
        return Mono.just(new DiagramTable(diagramId, tableId, size_x, size_y));
    }
}
