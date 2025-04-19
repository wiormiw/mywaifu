package com.silvermaiden.mywaifu.models.dtos.meta;

import java.util.List;

public record PagedResponseDTO<D>(
        List<D> content,    // The data for the current page
        int page,           // Current page number (0-based)
        int size,           // Items per page
        long totalElements, // Total items across all pages
        int totalPages,     // Total number of pages
        boolean hasNext,    // Is there a next page?
        boolean hasPrevious // Is there a previous page?
) {
}
