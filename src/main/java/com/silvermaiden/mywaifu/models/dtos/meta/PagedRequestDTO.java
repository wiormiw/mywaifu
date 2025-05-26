package com.silvermaiden.mywaifu.models.dtos.meta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import static com.silvermaiden.mywaifu.common.constants.ErrorMessage.INVALID_SIZE_OR_PAGE;
import static com.silvermaiden.mywaifu.common.constants.ErrorMessage.INVALID_SORT_DIRECTION;

public record PagedRequestDTO(
        @Min(value = 0, message = INVALID_SIZE_OR_PAGE)
        int page,

        @Min(value = 1, message = INVALID_SIZE_OR_PAGE)
        int size,

        String sortBy,

        @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE, message = INVALID_SORT_DIRECTION)
        String sortDirection
) {
    public PagedRequestDTO {
        page = page < 0 ? 0 : page;
        size = size < 1 ? 10 : size;
    }
}
