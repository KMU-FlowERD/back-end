package com.flowerd.backend.entity.dto.outbound;

import com.flowerd.backend.entity.enum_name.DATATYPE;
import com.flowerd.backend.entity.enum_name.ISKEY;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class ColumnReturns {

    private String id;

    private String columnName;

    private Boolean nullable; // Constraint

    private Boolean unique; // Constraint

    private ISKEY isKey; // Constraint

    private DATATYPE dataType;

    private Integer length;

    public ColumnReturns(ObjectId id, String columnName, Boolean nullable, Boolean unique, ISKEY isKey, DATATYPE dataType, Integer length) {
        this.id = id.toString();
        this.columnName = columnName;
        this.nullable = nullable;
        this.unique = unique;
        this.isKey = isKey;
        this.dataType = dataType;
        this.length = length;
    }
}
