package com.flowerd.backend.entity;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_USER("ROLE_USER"),

    // 익명
    ROLE_ANONYMOUS("ROLE_ANONYMOUS");

    final String role;

    Role(String role) {
        this.role = role;
    }
}
