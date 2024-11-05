package com.flowerd.backend.entity.dto.inbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class TableVO {
    private ObjectId schemaId;
    private ObjectId diagramId;
    private String tableName;
    private Long posX;
    private Long posY;
}
