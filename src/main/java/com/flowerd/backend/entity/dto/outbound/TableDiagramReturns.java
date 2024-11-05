package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
public class TableDiagramReturns {
    ObjectId id;
    String tableName;
    Long pos_x; // position x of table in diagram (nullable at Schema)
    Long pos_y; // position y of table in diagram (nullable at Schema)
    List<ColumnReturns> columns;
    List<ConstraintsReturns> constraints;
}
