package pl.project.benchmark.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.project.benchmark.common.dto.CreateOrderRequest;
import pl.project.benchmark.common.dto.OrderDto;
import pl.project.benchmark.common.exception.ResourceNotFoundException;
import pl.project.benchmark.common.mapper.OrderMapper;
import pl.project.benchmark.common.persistence.Book;
import pl.project.benchmark.common.persistence.Order;
import pl.project.benchmark.common.persistence.User;
import pl.project.benchmark.common.repository.BookRepository;
import pl.project.benchmark.common.repository.OrderRepository;
import pl.project.benchmark.common.repository.UserRepository;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public OrderDto getOrder(Long id) {

        return orderRepository.findDetailedById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + id
                        ));
    }

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        List<Book> books =
                bookRepository.findAllById(request.bookIds());

        Order order = new Order(user, books);

        Order saved = orderRepository.save(order);

        return OrderMapper.toDto(saved);
    }
}
