package com.flowerd.backend.security.filter;

import com.flowerd.backend.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements WebFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 토큰이 없거나 "Bearer "로 시작하지 않으면 익명 권한 부여
        // token==null 꼭 필요
        if (token == null || !token.startsWith("Bearer ")) {
            // 권한 부여 - ROLE_ANONYMOUS
            Authentication anonymousAuth = new UsernamePasswordAuthenticationToken(
                    "ROLE_ANONYMOUS", null, List.of((GrantedAuthority) () -> "ROLE_ANONYMOUS"));

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(anonymousAuth));
        }

        // "Bearer " 제거 후 토큰 추출
        token = token.substring(7);

        // 토큰 유효성 검사
        if (jwtTokenProvider.validateToken(token)) {
            // 토큰에서 권한 추출
            List<GrantedAuthority> authorities = jwtTokenProvider.getRole(token).stream()
                    .map(role -> (GrantedAuthority) () -> role)
                    .toList();

            // Authentication 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    jwtTokenProvider.getEmailForAccessToken(token), null, authorities);

            // SecurityContext에 Authentication 객체 저장
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        // 토큰이 유효하지 않으면 익명 권한 부여 후 필터 체인 계속 진행
        Authentication anonymousAuth = new UsernamePasswordAuthenticationToken(
                "ROLE_ANONYMOUS", null, List.of((GrantedAuthority) () -> "ROLE_ANONYMOUS"));

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(anonymousAuth));
    }
}

