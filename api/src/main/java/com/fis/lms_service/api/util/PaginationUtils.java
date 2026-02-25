package com.fis.lms_service.api.util;

import com.intern.hub.library.common.dto.PaginatedData;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static <T, R> PaginatedData<R> toPaginatedData(Page<T> page, Function<T, R> mapper) {
        var items = page.getContent().stream().map(mapper).toList();
        return PaginatedData.<R>builder()
                .items(items)
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
