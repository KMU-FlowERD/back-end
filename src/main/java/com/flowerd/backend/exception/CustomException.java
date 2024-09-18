package com.flowerd.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.flowerd.backend.entity.dto.ApiResponse;

@Getter
public class CustomException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final ApiResponse<String> response;

    public CustomException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.response = ApiResponse.fail(message);
    }
}
