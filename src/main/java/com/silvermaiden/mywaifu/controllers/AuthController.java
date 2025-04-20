package com.silvermaiden.mywaifu.controllers;

import com.silvermaiden.mywaifu.models.dtos.auth.AuthRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.http.ApiResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserRequestDTO;
import com.silvermaiden.mywaifu.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserServiceImpl userServiceImpl;

    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> register(@Valid @RequestBody UserRequestDTO req) {
        AuthResponseDTO res = userServiceImpl.register(req);
        return ResponseEntity.ok(ApiResponseDTO.success(res));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO req) {
        AuthResponseDTO res = userServiceImpl.login(req);
        return ResponseEntity.ok(ApiResponseDTO.success(res));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> refreshToken(@RequestBody String refreshToken) {
        AuthResponseDTO res = userServiceImpl.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponseDTO.success(res));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout() {
        userServiceImpl.logout();
        return ResponseEntity.ok(ApiResponseDTO.success(null));
    }
}
