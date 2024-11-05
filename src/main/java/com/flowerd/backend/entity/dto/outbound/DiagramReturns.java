package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
public class DiagramReturns {
    ObjectId id;
    String diagramName;
    Long pixel_x;
    Long pixel_y;
    String diagramContent;
    List<TableDiagramReturns> tables;
}
