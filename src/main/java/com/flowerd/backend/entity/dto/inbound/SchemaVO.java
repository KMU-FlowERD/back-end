package com.flowerd.backend.entity.dto.inbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class SchemaVO {
    private ObjectId projectId;
    private String schemaName;
}
