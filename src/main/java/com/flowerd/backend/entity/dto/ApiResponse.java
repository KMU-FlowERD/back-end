package com.flowerd.backend.entity.dto;

public record ApiResponse<T>(
        String status,
        T data,
        String message
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("success", null, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>("fail", null, message);
    }
}
