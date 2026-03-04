package com.bandhub.zsi.shared.api;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        String sortBy,
        String sortDir,
        String query
) {
    public static <T> PageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements,
            String sortBy,
            String sortDir,
            String query
    ) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(content, page, size, totalElements, totalPages, sortBy, sortDir, query);
    }
}
