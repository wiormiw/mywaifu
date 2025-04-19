package com.silvermaiden.mywaifu.models.mappers;

import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;
import com.silvermaiden.mywaifu.models.entities.User;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.silvermaiden.mywaifu.common.utilities.DateUtil.toLocaleDateTimeFromInstant;
import static com.silvermaiden.mywaifu.common.utilities.DateUtil.formatLocaleDateTime;

@Component
public class UserMapper implements GenericEntityDtoMapper<User, UserDTO> {
    @Override
    public UserDTO toDto(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                formatLocaleDateTime(toLocaleDateTimeFromInstant(user.getCreatedAt())),
                formatLocaleDateTime(toLocaleDateTimeFromInstant(user.getUpdatedAt()))
        );
    }

    @Override
    public List<UserDTO> toDtoList(List<User> users) {
        if (users == null) return List.of();
        return users.stream().map(this::toDto).toList();
    }

    @Override
    public PagedResponseDTO<UserDTO> toPagedResponse(Page<User> page) {
        if (page == null) return null;
        return new PagedResponseDTO<>(
                toDtoList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
