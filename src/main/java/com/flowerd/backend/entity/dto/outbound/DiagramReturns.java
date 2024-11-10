package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
public class DiagramReturns {
    String id;
    Long pixel_x;
    Long pixel_y;
    String diagramContent;
    List<TableDiagramReturns> tables;

    public DiagramReturns(ObjectId id, Long pixel_x, Long pixel_y, String diagramContent, List<TableDiagramReturns> tables) {
        this.id = id.toString();
        this.pixel_x = pixel_x;
        this.pixel_y = pixel_y;
        this.diagramContent = diagramContent;
        this.tables = tables;
    }
}
