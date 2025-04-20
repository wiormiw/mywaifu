package com.silvermaiden.mywaifu.services;

import com.silvermaiden.mywaifu.models.dtos.auth.AuthRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;

import java.util.List;

public interface UserService {
    AuthResponseDTO register(UserRequestDTO req);
    AuthResponseDTO login(AuthRequestDTO req);
    AuthResponseDTO refreshToken(String refreshToken);
    void logout();
    Long create(UserRequestDTO req);
    List<UserDTO> getAll();
    UserDTO getById(Long id);
    Long update(Long id, UserRequestDTO req);
    void delete(Long id);
    PagedResponseDTO<UserDTO> getPaged(int page, int size);
    PagedResponseDTO<UserDTO> getPagedSorted(int page, int size, String sortBy, String sortDirection);
    UserDTO getCurrentUser();
    Long updateCurrentUser(UserRequestDTO req);
}
