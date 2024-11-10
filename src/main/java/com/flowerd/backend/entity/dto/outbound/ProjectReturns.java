package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
public class ProjectReturns {
    String id;
    String projectName;
    List<SchemaReturns> schemaReturns;

    public ProjectReturns(ObjectId id, String projectName, List<SchemaReturns> schemaReturns) {
        this.id = id.toString();
        this.projectName = projectName;
        this.schemaReturns = schemaReturns;
    }
}
