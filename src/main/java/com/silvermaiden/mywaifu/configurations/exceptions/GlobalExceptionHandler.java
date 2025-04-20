package com.silvermaiden.mywaifu.configurations.exceptions;

import com.silvermaiden.mywaifu.configurations.exceptions.custom.CustomAuthenticationException;
import com.silvermaiden.mywaifu.models.dtos.http.ApiResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.silvermaiden.mywaifu.common.constants.ErrorCode.*;
import static com.silvermaiden.mywaifu.common.constants.HTTPConstant.DEFAULT_FAILED_MESSAGE;
import static com.silvermaiden.mywaifu.common.constants.ValidationConstant.DEFAULT_VALIDATION_ERROR_MESSAGE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handle @Valid exception in controller request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        String firstErrorMessage = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(DEFAULT_VALIDATION_ERROR_MESSAGE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDTO.error(firstErrorMessage, BAD_REQUEST));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalState(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.error(ex.getMessage(), UNAUTHORIZED));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ApiResponseDTO.error(ex.getMessage(), ENTITY_NOT_FOUND));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponseDTO.error(ex.getMessage(), ILLEGAL_ARGUMENT));
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleCustomAuthentication(CustomAuthenticationException ex) {
        log.error("CustomAuthenticationException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponseDTO.error(ex.getMessage(), BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        return ResponseEntity.status(500).body(ApiResponseDTO.error(DEFAULT_FAILED_MESSAGE, DEFAULT_ERROR_CODE));
    }
}
