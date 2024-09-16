package com.flowerd.backend.entity;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "member")
@Getter
public class Member {
    @Id
    private ObjectId id;

    private final String email;

    private final String password;

    private final String name;

    private final List<Role> roles = new ArrayList<>();

    private Member(String email, String name, String password, List<Role> roles) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.roles.addAll(roles);
    }

    public static Member of(
            String email,
            String name,
            String password,
            List<Role> roles
    ) {
        return new Member(email, name, password, roles);
    }
}
