package com.flowerd.backend.entity.dto.outbound;

import com.flowerd.backend.entity.enum_name.CARDINALITY;
import com.flowerd.backend.entity.enum_name.PARTICIPATION;
import com.flowerd.backend.entity.enum_name.RELTYPE;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class ConstraintsReturns {

    private String id;

    private String childColumnId;

    private String parentColumnId;

    private PARTICIPATION parentParticipation;

    private PARTICIPATION childParticipation;

    private CARDINALITY parentCardinality;

    private CARDINALITY childCardinality;

    private RELTYPE relType;

    public ConstraintsReturns(ObjectId id,ObjectId childColumnId, ObjectId parentColumnId, PARTICIPATION parentParticipation, PARTICIPATION childParticipation, CARDINALITY parentCardinality, CARDINALITY childCardinality, RELTYPE relType) {
        this.id = id.toString();
        this.childColumnId = childColumnId.toString();
        this.parentColumnId = parentColumnId.toString();
        this.parentParticipation = parentParticipation;
        this.childParticipation = childParticipation;
        this.parentCardinality = parentCardinality;
        this.childCardinality = childCardinality;
        this.relType = relType;
    }
}
