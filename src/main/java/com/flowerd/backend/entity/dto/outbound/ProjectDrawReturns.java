package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
public class ProjectDrawReturns {
    ObjectId id;
    String projectName;
    List<DiagramReturns> diagramReturns;
}
