package pl.project.benchmark.common.dto;

import java.util.List;

public record CreateOrderRequest(
        Long userId,
        List<Long> bookIds
) {
}
