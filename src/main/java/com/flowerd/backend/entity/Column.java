package com.flowerd.backend.entity;

import com.flowerd.backend.entity.enum_name.DATATYPE;
import com.flowerd.backend.entity.enum_name.ISKEY;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;


@Document(collection = "column")
@NoArgsConstructor
@Getter
@Setter
public class Column {

    @Id
    private ObjectId id;

    private ObjectId tableId;

    private String path;

    private String constraintName;

    private String columnName;

    private Boolean nullable; // Constraint

    private Boolean unique; // Constraint

    private ISKEY isKey; // Constraint

    private DATATYPE dataType;

    private Integer length;


    public Column(ObjectId tableId, String path, String constraintName,  String columnName, Boolean nullable, Boolean unique, ISKEY isKey, DATATYPE dataType, Integer length) {
        this.tableId = tableId;
        this.path = path;
        this.constraintName = constraintName;
        this.columnName = columnName;
        this.nullable = nullable;
        this.unique = unique;
        this.isKey = isKey;
        this.dataType = dataType;
        this.length = length;
    }

    public Mono<Column> of(ObjectId tableId, String path, String constraintName,  String columnName, Boolean nullable, Boolean unique, ISKEY isKey, DATATYPE dataType, Integer length) {
        return Mono.just(new Column(tableId, path, constraintName, columnName, nullable, unique, isKey, dataType, length));
    }
}
