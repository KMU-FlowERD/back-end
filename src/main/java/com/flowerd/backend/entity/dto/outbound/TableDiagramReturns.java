package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
public class TableDiagramReturns {
    String id;
    String tableName;
    Long pos_x;
    Long pos_y;
    List<ColumnReturns> columns;
    List<ConstraintsReturns> constraints;

    public TableDiagramReturns(ObjectId id, String tableName, Long pos_x, Long pos_y, List<ColumnReturns> columns, List<ConstraintsReturns> constraints) {
        this.id = id.toString();
        this.tableName = tableName;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.columns = columns;
        this.constraints = constraints;
    }
}
