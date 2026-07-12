package pl.project.benchmark.grpc.service;

import bookstore.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pl.project.benchmark.common.dto.CreateOrderRequest;
import pl.project.benchmark.common.dto.OrderDto;
import pl.project.benchmark.common.service.BookService;
import pl.project.benchmark.common.service.OrderService;
import pl.project.benchmark.common.service.UserService;

@GrpcService
public class BookstoreGrpcService
        extends BookstoreServiceGrpc.BookstoreServiceImplBase {

    private final UserService userService;
    private final BookService bookService;
    private final OrderService orderService;

    public BookstoreGrpcService(
            UserService userService,
            BookService bookService,
            OrderService orderService
    ) {
        this.userService = userService;
        this.bookService = bookService;
        this.orderService = orderService;
    }

    @Override
    public void getUser(
            UserRequest request,
            StreamObserver<UserResponse> observer
    ) {

        var user = userService.getUser(request.getId());

        observer.onNext(
                UserResponse.newBuilder()
                        .setId(user.id())
                        .setName(user.name())
                        .setEmail(user.email())
                        .build()
        );

        observer.onCompleted();
    }

    @Override
    public void getBooks(
            Empty request,
            StreamObserver<BookList> observer
    ) {

        BookList.Builder builder =
                BookList.newBuilder();

        bookService.getBooks()
                .forEach(book ->
                        builder.addBooks(
                                BookResponse.newBuilder()
                                        .setId(book.id())
                                        .setTitle(book.title())
                                        .setAuthor(book.author())
                                        .setPrice(book.price().doubleValue())
                                        .build()
                        ));

        observer.onNext(builder.build());
        observer.onCompleted();
    }

    @Override
    public void createOrder(
            CreateOrderRequestGrpc request,
            StreamObserver<OrderResponse> observer
    ) {

        OrderDto order =
                orderService.createOrder(
                        new CreateOrderRequest(
                                request.getUserId(),
                                request.getBookIdsList()
                        )
                );

        observer.onNext(toResponse(order));
        observer.onCompleted();
    }

    private OrderResponse toResponse(
            OrderDto order
    ) {

        OrderResponse.Builder builder =
                OrderResponse.newBuilder()
                        .setId(order.id())
                        .setCreatedAt(
                                order.createdAt().toString()
                        );

        builder.setUser(
                UserResponse.newBuilder()
                        .setId(order.user().id())
                        .setName(order.user().name())
                        .setEmail(order.user().email())
                        .build()
        );

        order.books().forEach(book ->
                builder.addBooks(
                        BookResponse.newBuilder()
                                .setId(book.id())
                                .setTitle(book.title())
                                .setAuthor(book.author())
                                .setPrice(
                                        book.price().doubleValue()
                                )
                                .build()
                ));

        return builder.build();
    }
}
