package com.flowerd.backend.entity.dto.outbound;

import com.flowerd.backend.entity.enum_name.DATATYPE;
import com.flowerd.backend.entity.enum_name.ISKEY;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class ColumnReturns {

    private ObjectId id;

    private String columnName;

    private Boolean nullable; // Constraint

    private Boolean unique; // Constraint

    private ISKEY isKey; // Constraint

    private DATATYPE dataType;

    private Integer length;
}
