package com.flowerd.backend.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    @Value("${jwt.refresh_expiration}")
    private String refreshExpire;

    //  RefreshToken 을 쿠키에 저장.
    public ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true)
                .maxAge(Long.parseLong(refreshExpire))
                .path("/")
                .build();
    }

    // 쿠키에 저장된 RefreshToken을 삭제.
    public ResponseCookie deleteRefreshCookie() {
        return ResponseCookie.from("RefreshToken", null)
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();
    }

}
