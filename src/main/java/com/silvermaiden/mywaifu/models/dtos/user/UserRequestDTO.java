package com.silvermaiden.mywaifu.models.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.silvermaiden.mywaifu.common.constants.RegexConstant.REGEX_NO_BOOLEAN_AND_NUMBER;
import static com.silvermaiden.mywaifu.common.constants.ValidationConstant.*;

public record UserRequestDTO(
        @NotBlank(message = FIELD_REQUIRED_MESSAGE + "username")
        @Pattern(regexp = REGEX_NO_BOOLEAN_AND_NUMBER, message = NO_NUMBER_AND_BOOLEAN + "username")
        String username,
        @NotBlank(message = FIELD_REQUIRED_MESSAGE + "name")
        @Pattern(regexp = REGEX_NO_BOOLEAN_AND_NUMBER, message = NO_NUMBER_AND_BOOLEAN + "name")
        String name,
        @NotBlank(message = FIELD_REQUIRED_MESSAGE + "password")
        @Pattern(regexp = REGEX_NO_BOOLEAN_AND_NUMBER, message = NO_NUMBER_AND_BOOLEAN + "password")
        @Pattern(
                regexp = "^(?=.*[A-Z])[A-Za-z0-9]+$",
                message = INVALID_PASSWORD_MESSAGE
        )
        String password,
        @NotBlank(message = FIELD_REQUIRED_MESSAGE + "email")
        @Pattern(regexp = REGEX_NO_BOOLEAN_AND_NUMBER, message = NO_NUMBER_AND_BOOLEAN + "email")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = INVALID_EMAIL_MESSAGE
        )
        String email
) {
}
