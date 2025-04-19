package com.silvermaiden.mywaifu.models.dtos.auth;

import jakarta.validation.constraints.NotBlank;

import static com.silvermaiden.mywaifu.common.constants.ValidationConstant.LOGIN_VALIDATION_MESSAGE;

public record AuthRequestDTO(
        @NotBlank(message = LOGIN_VALIDATION_MESSAGE)
        String username,
        @NotBlank(message = LOGIN_VALIDATION_MESSAGE)
        String password
) {
}
