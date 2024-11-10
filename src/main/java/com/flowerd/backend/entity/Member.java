package com.flowerd.backend.entity;

import com.flowerd.backend.entity.enum_name.ROLE;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "member")
@Getter
@Setter
public class Member {

    @Id
    private ObjectId id;

    // email은 unique해야 하므로 CompoundIndex를 사용하여 unique하게 설정
    private final String email;

    private final String password;

    private final String name;

    private final List<ROLE> ROLES = new ArrayList<>();


    private Member(String email, String name, String password, List<ROLE> ROLES) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.ROLES.addAll(ROLES);
    }

    public static Member of(
            String email,
            String name,
            String password,
            List<ROLE> ROLES
    ) {
        return new Member(email, name, password, ROLES);
    }
}
