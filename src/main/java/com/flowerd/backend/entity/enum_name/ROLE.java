package com.flowerd.backend.entity.enum_name;

import lombok.Getter;

@Getter
public enum ROLE {

    ROLE_USER("ROLE_USER"),

    // 익명
    ROLE_ANONYMOUS("ROLE_ANONYMOUS");

    final String role;

    ROLE(String role) {
        this.role = role;
    }
}
