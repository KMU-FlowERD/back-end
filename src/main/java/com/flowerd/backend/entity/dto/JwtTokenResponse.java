package com.flowerd.backend.entity.dto;

public record JwtTokenResponse (
        String accessToken,
        String refreshToken
) {
    public static JwtTokenResponse toResponse(String accessToken, String refreshToken) {
        return new JwtTokenResponse(accessToken, refreshToken);
    }
}
