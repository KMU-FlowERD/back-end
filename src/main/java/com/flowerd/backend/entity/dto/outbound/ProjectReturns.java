package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
public class ProjectReturns {
    ObjectId id;
    String projectName;
    List<SchemaReturns> schemaReturns;
}
