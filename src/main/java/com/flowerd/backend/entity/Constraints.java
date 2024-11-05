package com.flowerd.backend.entity;

import com.flowerd.backend.entity.enum_name.CARDINALITY;
import com.flowerd.backend.entity.enum_name.PARTICIPATION;
import com.flowerd.backend.entity.enum_name.RELTYPE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

@Document(collection = "Constraint")
@NoArgsConstructor
@Getter
@Setter
public class Constraints {

    @Id
    private ObjectId id;

    private ObjectId childTableId;

    private ObjectId childColumnId;

    private ObjectId parentTableId;

    private ObjectId parentColumnId;

    private PARTICIPATION parentParticipation;

    private PARTICIPATION childParticipation;

    private CARDINALITY parentCardinality;

    private CARDINALITY childCardinality;

    private RELTYPE relType; // 모두가 하나라도 식별이면 식별관계, 그렇지 않다면 비식별관계

    public Constraints(ObjectId childColumnId, ObjectId parentColumnId, PARTICIPATION parentParticipation, PARTICIPATION childParticipation, CARDINALITY parentCardinality, CARDINALITY childCardinality, RELTYPE relType) {
        this.childColumnId = childColumnId;
        this.parentColumnId = parentColumnId;
        this.parentParticipation = parentParticipation;
        this.childParticipation = childParticipation;
        this.parentCardinality = parentCardinality;
        this.childCardinality = childCardinality;
        this.relType = relType;
    }

    public Mono<Constraints> of(ObjectId childColumnId, ObjectId parentColumnId, PARTICIPATION parentParticipation, PARTICIPATION childParticipation, CARDINALITY parentCardinality, CARDINALITY childCardinality, RELTYPE relType) {
        return Mono.just(new Constraints(childColumnId, parentColumnId, parentParticipation, childParticipation, parentCardinality, childCardinality, relType));
    }
}
