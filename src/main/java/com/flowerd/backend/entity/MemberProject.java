package com.flowerd.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

@Document(collection = "member_project")
@NoArgsConstructor
@Getter
public class MemberProject {

    @Id
    private ObjectId id;

    @DBRef
    private ObjectId memberId;

    @DBRef
    private ObjectId projectId;

    MemberProject(ObjectId memberId, ObjectId projectId) {
        this.memberId = memberId;
        this.projectId = projectId;
    }

    public static Mono<MemberProject> of(ObjectId memberId, ObjectId projectId) {
        return Mono.just(new MemberProject(memberId, projectId));
    }
}
