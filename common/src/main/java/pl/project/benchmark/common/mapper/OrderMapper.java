package pl.project.benchmark.common.mapper;

import pl.project.benchmark.common.dto.OrderDto;
import pl.project.benchmark.common.persistence.Order;

public class OrderMapper {

    public static OrderDto toDto(Order order) {

        return new OrderDto(
                order.getId(),
                UserMapper.toDto(order.getUser()),
                order.getBooks()
                        .stream()
                        .map(BookMapper::toDto)
                        .toList(),
                order.getCreatedAt()
        );
    }
}
