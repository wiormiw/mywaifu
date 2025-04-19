package com.silvermaiden.mywaifu.models.mappers;

import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;

import org.springframework.data.domain.Page;
import java.util.List;

public interface GenericEntityDtoMapper<E, D> {
    D toDto(E entity);
    List<D> toDtoList(List<E> entities);

    default PagedResponseDTO<D> toPagedResponse(Page<E> entities) {
        if (entities == null) {
            return new PagedResponseDTO<>(List.of(), 0, 0, 0, 0, false, false);
        }
        List<D> content = entities.getContent().stream()
                .map(this::toDto)
                .toList();
        return new PagedResponseDTO<>(
                content,
                entities.getNumber(),
                entities.getSize(),
                entities.getTotalElements(),
                entities.getTotalPages(),
                entities.hasNext(),
                entities.hasPrevious()
        );
    }
}
