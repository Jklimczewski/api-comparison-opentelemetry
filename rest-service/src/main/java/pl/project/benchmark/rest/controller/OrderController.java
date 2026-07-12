package pl.project.benchmark.rest.controller;

import org.springframework.web.bind.annotation.*;
import pl.project.benchmark.common.dto.CreateOrderRequest;
import pl.project.benchmark.common.dto.OrderDto;
import pl.project.benchmark.common.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public OrderDto getOrder(
            @PathVariable Long id
    ) {
        return service.getOrder(id);
    }

    @PostMapping
    public OrderDto createOrder(
            @RequestBody CreateOrderRequest request
    ) {
        return service.createOrder(request);
    }
}
