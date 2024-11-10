package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
public class SchemaReturns {
    String SchemaId;
    String schemaName;
    List<TableReturns> tableReturns;

    public SchemaReturns(ObjectId schemaId, String schemaName, List<TableReturns> tableReturns) {
        this.SchemaId = schemaId.toString();
        this.schemaName = schemaName;
        this.tableReturns = tableReturns;
    }
}
