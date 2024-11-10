package com.flowerd.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

@Document(collection = "schema")
@NoArgsConstructor
@Getter
@Setter
public class Schema {

    @Id
    private ObjectId id;

    private ObjectId projectId;

    private String schemaName;

    public Schema(ObjectId projectId, String schemaName) {
        this.projectId = projectId;
        this.schemaName = schemaName;
    }

    public static Mono<Schema> of(ObjectId projectId, String schemaName) {
        return Mono.just(new Schema(projectId, schemaName));
    }
}
