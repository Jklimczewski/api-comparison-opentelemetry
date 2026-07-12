package pl.project.benchmark.graphql.controller;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import pl.project.benchmark.common.dto.CreateOrderRequest;
import pl.project.benchmark.common.dto.OrderDto;
import pl.project.benchmark.common.service.OrderService;

@Controller
public class MutationResolver {

    private final OrderService service;

    public MutationResolver(
            OrderService service
    ) {
        this.service = service;
    }

    @MutationMapping
    public OrderDto createOrder(
            @Argument CreateOrderInput input
    ) {

        return service.createOrder(
                new CreateOrderRequest(
                        input.userId(),
                        input.bookIds()
                )
        );
    }

    public record CreateOrderInput(
            Long userId,
            java.util.List<Long> bookIds
    ) {}
}
