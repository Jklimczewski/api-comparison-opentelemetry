package pl.project.benchmark.graphql.controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import pl.project.benchmark.common.dto.BookDto;
import pl.project.benchmark.common.dto.OrderDto;
import pl.project.benchmark.common.dto.UserDto;
import pl.project.benchmark.common.service.BookService;
import pl.project.benchmark.common.service.OrderService;
import pl.project.benchmark.common.service.UserService;


@Controller
public class QueryResolver {

    private final UserService userService;
    private final BookService bookService;
    private final OrderService orderService;

    public QueryResolver(
            UserService userService,
            BookService bookService,
            OrderService orderService
    ) {
        this.userService = userService;
        this.bookService = bookService;
        this.orderService = orderService;
    }

    @QueryMapping
    public UserDto user(
            @Argument Long id
    ) {
        return userService.getUser(id);
    }

    @QueryMapping
    public List<BookDto> books() {
        return bookService.getBooks();
    }

    @QueryMapping
    public OrderDto order(
            @Argument Long id
    ) {
        return orderService.getOrder(id);
    }
}
