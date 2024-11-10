package com.flowerd.backend.entity.dto.outbound;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class ProjectListReturns {
    String id;
    String projectName;

    public ProjectListReturns(ObjectId id, String projectName) {
        this.id = id.toString();
        this.projectName = projectName;
    }
}
