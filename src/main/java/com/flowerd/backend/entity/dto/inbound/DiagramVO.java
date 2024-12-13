package com.flowerd.backend.entity.dto.inbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class DiagramVO {
    private ObjectId projectId;
    private ObjectId schemaId;
    private String diagramName;
    private String diagramContent;
    private Long SizeX;
    private Long SizeY;
}
