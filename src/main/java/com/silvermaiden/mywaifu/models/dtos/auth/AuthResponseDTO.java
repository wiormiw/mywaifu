package com.silvermaiden.mywaifu.models.dtos.auth;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        long expiresAt
) {
}
