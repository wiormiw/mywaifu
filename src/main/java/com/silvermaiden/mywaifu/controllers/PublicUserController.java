package com.silvermaiden.mywaifu.controllers;

import com.silvermaiden.mywaifu.models.dtos.http.ApiResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserRequestDTO;
import com.silvermaiden.mywaifu.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users/me")
@PreAuthorize("hasRole('USER')")
public class PublicUserController {
    private final UserServiceImpl userServiceImpl;

    public PublicUserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<UserDTO>> getMyDetail() {
        UserDTO user = userServiceImpl.getCurrentUser();
        return ResponseEntity.ok(ApiResponseDTO.success(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Long>> update(@Valid @RequestBody UserRequestDTO req) {
        Long updatedId = userServiceImpl.updateCurrentUser(req);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedId));
    }
}
