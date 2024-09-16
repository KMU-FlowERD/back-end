package com.flowerd.backend.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowerd.backend.security.filter.AuthenticationFilter;
import com.flowerd.backend.security.filter.AuthorizationFilter;
import com.flowerd.backend.security.util.CookieProvider;
import com.flowerd.backend.security.util.JwtTokenProvider;
import com.flowerd.backend.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.reactive.CorsWebFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsWebFilter corsWebFilter;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // AuthenticationFilter는 WebFilter로 작성됨
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(reactiveAuthenticationManager, cookieProvider, jwtTokenProvider, tokenService, objectMapper);

        // AuthorizationFilter는 WebFilter로 작성됨
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(jwtTokenProvider);

        return http
                // 예외 처리 필터 추가
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .anyExchange().permitAll() // 모든 요청 허용
                )
                // 예외 처리
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((swe, e) -> Mono.error(e)) // 커스텀 예외 처리 가능
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutHandler((exchange, authentication) -> {
                            exchange.getExchange().getResponse().addCookie(cookieProvider.deleteRefreshCookie());
                            return Mono.empty();
                        })
                )
                // 필터 추가: CORS 필터, 인증 및 인가 필터
                .addFilterAt(corsWebFilter, SecurityWebFiltersOrder.CORS)
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(authorizationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }
}
