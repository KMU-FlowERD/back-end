package com.flowerd.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "project")
@NoArgsConstructor
@Getter
@Setter
public class Project {

    @Id
    private ObjectId id;

    private String projectName;

    private ObjectId ownerId;


    // 프로젝트 생성시, MemberProject 생성.
    public Project(String projectName, ObjectId ownerId) {
        this.projectName = projectName;
        this.ownerId = ownerId;
    }

    public static Mono<Project> of(String projectName, ObjectId ownerId) {
        return Mono.just(new Project(projectName, ownerId));
    }
}
