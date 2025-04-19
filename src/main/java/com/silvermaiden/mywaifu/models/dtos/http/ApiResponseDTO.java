package com.silvermaiden.mywaifu.models.dtos.http;

import com.silvermaiden.mywaifu.common.utilities.DateUtil;

import java.time.Instant;
import java.time.LocalDateTime;

public record ApiResponseDTO<T>(
        String status,    // "success" or "error"
        T data,           // EntityDto, PagedResponse<EntityDto>, etc.
        String message,   // Optional error message or success note
        String timestamp, // Response timestamp
        String errorCode  // Response error code (if any)
) {
    // Factory for success
    public static <T> ApiResponseDTO<T> success(T data) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), DateUtil.DEFAULT_ZONE);
        String formattedTimestamp = now.format(DateUtil.dateTimeFormatter());
        return new ApiResponseDTO<>("success", data, null, formattedTimestamp, null);
    }

    // Factory for error
    public static <T> ApiResponseDTO<T> error(String message, String errorCode) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), DateUtil.DEFAULT_ZONE);
        String formattedTimestamp = now.format(DateUtil.dateTimeFormatter());
        return new ApiResponseDTO<>("error", null, message, formattedTimestamp, errorCode);
    }
}
