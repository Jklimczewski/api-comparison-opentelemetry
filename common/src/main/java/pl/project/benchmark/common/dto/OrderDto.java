package pl.project.benchmark.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        UserDto user,
        List<BookDto> books,
        LocalDateTime createdAt
) {
}
