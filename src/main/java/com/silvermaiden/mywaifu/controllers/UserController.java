package com.silvermaiden.mywaifu.controllers;

import com.silvermaiden.mywaifu.models.dtos.http.ApiResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.meta.PagedRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;
import com.silvermaiden.mywaifu.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Long>> create(@Valid @RequestBody UserRequestDTO req) {
        Long id = this.userServiceImpl.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<UserDTO>>> getAll() {
        List<UserDTO> users = this.userServiceImpl.getAll();
        return ResponseEntity.ok(ApiResponseDTO.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getById(@PathVariable Long id) {
        UserDTO user = this.userServiceImpl.getById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Long>> update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO req) {
        Long updatedId = this.userServiceImpl.update(id, req);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        this.userServiceImpl.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<UserDTO>>> getPagedSorted(
            @Valid @ModelAttribute PagedRequestDTO params) {
        PagedResponseDTO<UserDTO> res;
        if (params.sortBy() != null && params.sortDirection() != null) {
            res = this.userServiceImpl.getPagedSorted(
                    params.page(),
                    params.size(),
                    params.sortBy(),
                    params.sortDirection()
            );
        } else {
            res = this.userServiceImpl.getPaged(
                    params.page(),
                    params.size()
            );
        }
        return ResponseEntity.ok(ApiResponseDTO.success(res));
    }
}
