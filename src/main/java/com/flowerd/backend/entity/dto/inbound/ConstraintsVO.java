package com.flowerd.backend.entity.dto.inbound;

import com.flowerd.backend.entity.enum_name.CARDINALITY;
import com.flowerd.backend.entity.enum_name.PARTICIPATION;
import com.flowerd.backend.entity.enum_name.RELTYPE;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class ConstraintsVO {

    private ObjectId childTableId;

    private ObjectId childColumnId;

    private ObjectId parentTableId;

    private ObjectId parentColumnId;

    private PARTICIPATION parentParticipation;

    private PARTICIPATION childParticipation;

    private CARDINALITY parentCardinality;

    private CARDINALITY childCardinality;

    private RELTYPE relType;
}
