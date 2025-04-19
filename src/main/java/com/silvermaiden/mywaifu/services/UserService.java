package com.silvermaiden.mywaifu.services;

import com.silvermaiden.mywaifu.models.dtos.auth.AuthRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserCreateDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    public AuthResponseDTO register(UserCreateDTO req);
    public AuthResponseDTO login(AuthRequestDTO req);
    public AuthResponseDTO refreshToken(String refreshToken);
    public void logout();
    public Long create(UserCreateDTO req);
    public List<UserDTO> getAll();
    public UserDTO getById(Long id);
    public Long update(Long id, UserUpdateDTO req);
    public void delete(Long id);
    public PagedResponseDTO<UserDTO> getPaged(int page, int size);
    public PagedResponseDTO<UserDTO> getPagedSorted(int page, int size, String sortBy, String sortDirection);
    public UserDTO getCurrentUser();
    public Long updateCurrentUser(UserUpdateDTO req);
}
