package com.flowerd.backend.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowerd.backend.entity.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilterExceptionHandler implements WebFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(CustomException.class, e -> {
                    log.error("FilterExceptionHandler: " + e.getResponse());
                    return setErrorResponse(exchange.getResponse(), e.getResponse());
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("FilterExceptionHandler: 필터 예외가 발생했습니다." + Arrays.toString(e.getStackTrace()));
                    ApiResponse<?> err = ApiResponse.fail("필터 내부의 예외가 발생했습니다.");
                    return setErrorResponse(exchange.getResponse(), err);
                });
    }

    private Mono<Void> setErrorResponse(ServerHttpResponse response, ApiResponse<?> err) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            DataBuffer dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(err));
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}

