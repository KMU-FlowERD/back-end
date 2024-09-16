package com.flowerd.backend.entity.dto;

public record MemberRequest(
        String email,
        String password,
        String name
) {

}
