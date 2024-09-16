package com.flowerd.backend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowerd.backend.entity.dto.ApiResponse;
import com.flowerd.backend.entity.dto.LoginRequest;
import com.flowerd.backend.security.util.CookieProvider;
import com.flowerd.backend.security.util.JwtTokenProvider;
import com.flowerd.backend.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements WebFilter {

    private final ReactiveAuthenticationManager authenticationManager;
    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 요청이 로그인 요청인지 확인 (예시로 /login 경로 체크)
        if ("/login".equals(request.getPath().value()) && HttpMethod.POST.equals(request.getMethod())) {
            return DataBufferUtils.join(request.getBody()) // Flux<DataBuffer>를 결합하여 하나의 DataBuffer로 만듦
                    .map(buffer -> {
                        // LoginRequest를 JSON에서 읽어옴
                        try {
                            return objectMapper.readValue(buffer.asInputStream(), LoginRequest.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .flatMap(loginRequest -> {
                        // 로그인 인증을 시도
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                loginRequest.email(), loginRequest.password());
                        return authenticationManager.authenticate(authentication);
                    })
                    .flatMap(auth -> successfulAuthentication(exchange, auth)) // 인증 성공 시 처리
                    .onErrorResume(e -> unsuccessfulAuthentication(exchange, e)); // 인증 실패 시 처리
        }

        return chain.filter(exchange); // 다른 요청은 그대로 필터 체인을 진행
    }

    private Mono<Void> successfulAuthentication(ServerWebExchange exchange, Authentication authResult) {
        ServerHttpResponse response = exchange.getResponse();
        String email = authResult.getName();
        List<String> roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = jwtTokenProvider.createAccessToken(email, roles);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // RefreshToken을 Redis에 저장
        return tokenService.updateRefreshToken(email, refreshToken)
                .then(Mono.defer(() -> {
                    // RefreshToken을 쿠키에 저장
                    ResponseCookie refreshCookie = cookieProvider.createRefreshCookie(refreshToken);
                    response.addCookie(refreshCookie);

                    // AccessToken을 헤더에 추가
                    response.getHeaders().add("Authorization", accessToken);

                    // 로그인 성공 메시지
                    response.setStatusCode(HttpStatus.OK);
                    return response.writeWith(Mono.fromSupplier(() -> {
                        try {
                            byte[] bytes = objectMapper.writeValueAsBytes(ApiResponse.success("로그인 성공"));
                            return response.bufferFactory().wrap(bytes);
                        } catch (IOException e) {
                            throw new RuntimeException("Response writing error");
                        }
                    }));
                }));
    }

    private Mono<Void> unsuccessfulAuthentication(ServerWebExchange exchange, Throwable e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.fromSupplier(() -> {
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(ApiResponse.fail("로그인 실패"));
                return response.bufferFactory().wrap(bytes);
            } catch (IOException ex) {
                throw new RuntimeException("Response writing error");
            }
        }));
    }
}
