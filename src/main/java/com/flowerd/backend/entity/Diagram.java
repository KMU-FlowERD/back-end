package com.flowerd.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "diagram")
@NoArgsConstructor
@Getter
@Setter
public class Diagram {

    @Id
    private ObjectId id;

    private ObjectId projectId;

    private ObjectId schemaId;

    private String diagramId;

    private Long pixel_x;

    private Long pixel_y;

    private String diagramContent;


    public Diagram(ObjectId projectId, ObjectId schemaId, String diagramId, Long pixel_x, Long pixel_y, String diagramContent) {
        this.projectId = projectId;
        this.schemaId = schemaId;
        this.diagramId = diagramId;
        this.pixel_x = pixel_x;
        this.pixel_y = pixel_y;
        this.diagramContent = diagramContent;
    }

    public static Mono<Diagram> of(ObjectId projectId, String diagramId, ObjectId schemaId, Long pixel_x, Long pixel_y, String diagramContent) {
        return Mono.just(new Diagram(projectId, schemaId, diagramId, pixel_x, pixel_y, diagramContent));
    }

}
