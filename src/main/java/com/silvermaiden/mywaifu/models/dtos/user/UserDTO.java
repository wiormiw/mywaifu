package com.silvermaiden.mywaifu.models.dtos.user;

import java.util.Set;

public record UserDTO(
        Long id,
        String username,
        String name,
        String email,
        Set<String> roles,
        String createdAt, // Formatted as "MM-dd-yyyy HH:mm:ss"
        String updatedAt // Formatted as "MM-dd-yyyy HH:mm:ss"
) {
}
