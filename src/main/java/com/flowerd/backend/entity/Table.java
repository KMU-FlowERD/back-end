package com.flowerd.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;


@Document(collection = "table")
@NoArgsConstructor
@Getter
@Setter
public class Table {

    @Id
    private ObjectId id;

    private ObjectId SchemaId;

    private String tableName;


    public Table(ObjectId SchemaId, String tableName) {
        this.SchemaId = SchemaId;
        this.tableName = tableName;
    }

    public static Mono<Table> of(ObjectId SchemaId, String tableName) {
        return Mono.just(new Table(SchemaId, tableName));
    }

}
