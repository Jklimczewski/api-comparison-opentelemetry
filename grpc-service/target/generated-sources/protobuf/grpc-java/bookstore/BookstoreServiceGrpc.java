package bookstore;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.65.1)",
    comments = "Source: bookstore.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class BookstoreServiceGrpc {

  private BookstoreServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "bookstore.BookstoreService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<bookstore.UserRequest,
      bookstore.UserResponse> getGetUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUser",
      requestType = bookstore.UserRequest.class,
      responseType = bookstore.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bookstore.UserRequest,
      bookstore.UserResponse> getGetUserMethod() {
    io.grpc.MethodDescriptor<bookstore.UserRequest, bookstore.UserResponse> getGetUserMethod;
    if ((getGetUserMethod = BookstoreServiceGrpc.getGetUserMethod) == null) {
      synchronized (BookstoreServiceGrpc.class) {
        if ((getGetUserMethod = BookstoreServiceGrpc.getGetUserMethod) == null) {
          BookstoreServiceGrpc.getGetUserMethod = getGetUserMethod =
              io.grpc.MethodDescriptor.<bookstore.UserRequest, bookstore.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.UserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BookstoreServiceMethodDescriptorSupplier("GetUser"))
              .build();
        }
      }
    }
    return getGetUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bookstore.Empty,
      bookstore.BookList> getGetBooksMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBooks",
      requestType = bookstore.Empty.class,
      responseType = bookstore.BookList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bookstore.Empty,
      bookstore.BookList> getGetBooksMethod() {
    io.grpc.MethodDescriptor<bookstore.Empty, bookstore.BookList> getGetBooksMethod;
    if ((getGetBooksMethod = BookstoreServiceGrpc.getGetBooksMethod) == null) {
      synchronized (BookstoreServiceGrpc.class) {
        if ((getGetBooksMethod = BookstoreServiceGrpc.getGetBooksMethod) == null) {
          BookstoreServiceGrpc.getGetBooksMethod = getGetBooksMethod =
              io.grpc.MethodDescriptor.<bookstore.Empty, bookstore.BookList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBooks"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.BookList.getDefaultInstance()))
              .setSchemaDescriptor(new BookstoreServiceMethodDescriptorSupplier("GetBooks"))
              .build();
        }
      }
    }
    return getGetBooksMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bookstore.OrderRequest,
      bookstore.OrderResponse> getGetOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetOrder",
      requestType = bookstore.OrderRequest.class,
      responseType = bookstore.OrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bookstore.OrderRequest,
      bookstore.OrderResponse> getGetOrderMethod() {
    io.grpc.MethodDescriptor<bookstore.OrderRequest, bookstore.OrderResponse> getGetOrderMethod;
    if ((getGetOrderMethod = BookstoreServiceGrpc.getGetOrderMethod) == null) {
      synchronized (BookstoreServiceGrpc.class) {
        if ((getGetOrderMethod = BookstoreServiceGrpc.getGetOrderMethod) == null) {
          BookstoreServiceGrpc.getGetOrderMethod = getGetOrderMethod =
              io.grpc.MethodDescriptor.<bookstore.OrderRequest, bookstore.OrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.OrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.OrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BookstoreServiceMethodDescriptorSupplier("GetOrder"))
              .build();
        }
      }
    }
    return getGetOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bookstore.CreateOrderRequestGrpc,
      bookstore.OrderResponse> getCreateOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateOrder",
      requestType = bookstore.CreateOrderRequestGrpc.class,
      responseType = bookstore.OrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bookstore.CreateOrderRequestGrpc,
      bookstore.OrderResponse> getCreateOrderMethod() {
    io.grpc.MethodDescriptor<bookstore.CreateOrderRequestGrpc, bookstore.OrderResponse> getCreateOrderMethod;
    if ((getCreateOrderMethod = BookstoreServiceGrpc.getCreateOrderMethod) == null) {
      synchronized (BookstoreServiceGrpc.class) {
        if ((getCreateOrderMethod = BookstoreServiceGrpc.getCreateOrderMethod) == null) {
          BookstoreServiceGrpc.getCreateOrderMethod = getCreateOrderMethod =
              io.grpc.MethodDescriptor.<bookstore.CreateOrderRequestGrpc, bookstore.OrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.CreateOrderRequestGrpc.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bookstore.OrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BookstoreServiceMethodDescriptorSupplier("CreateOrder"))
              .build();
        }
      }
    }
    return getCreateOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BookstoreServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BookstoreServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BookstoreServiceStub>() {
        @java.lang.Override
        public BookstoreServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BookstoreServiceStub(channel, callOptions);
        }
      };
    return BookstoreServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BookstoreServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BookstoreServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BookstoreServiceBlockingStub>() {
        @java.lang.Override
        public BookstoreServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BookstoreServiceBlockingStub(channel, callOptions);
        }
      };
    return BookstoreServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BookstoreServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BookstoreServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BookstoreServiceFutureStub>() {
        @java.lang.Override
        public BookstoreServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BookstoreServiceFutureStub(channel, callOptions);
        }
      };
    return BookstoreServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getUser(bookstore.UserRequest request,
        io.grpc.stub.StreamObserver<bookstore.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserMethod(), responseObserver);
    }

    /**
     */
    default void getBooks(bookstore.Empty request,
        io.grpc.stub.StreamObserver<bookstore.BookList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetBooksMethod(), responseObserver);
    }

    /**
     */
    default void getOrder(bookstore.OrderRequest request,
        io.grpc.stub.StreamObserver<bookstore.OrderResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrderMethod(), responseObserver);
    }

    /**
     */
    default void createOrder(bookstore.CreateOrderRequestGrpc request,
        io.grpc.stub.StreamObserver<bookstore.OrderResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateOrderMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service BookstoreService.
   */
  public static abstract class BookstoreServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return BookstoreServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service BookstoreService.
   */
  public static final class BookstoreServiceStub
      extends io.grpc.stub.AbstractAsyncStub<BookstoreServiceStub> {
    private BookstoreServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BookstoreServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BookstoreServiceStub(channel, callOptions);
    }

    /**
     */
    public void getUser(bookstore.UserRequest request,
        io.grpc.stub.StreamObserver<bookstore.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getBooks(bookstore.Empty request,
        io.grpc.stub.StreamObserver<bookstore.BookList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetBooksMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getOrder(bookstore.OrderRequest request,
        io.grpc.stub.StreamObserver<bookstore.OrderResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createOrder(bookstore.CreateOrderRequestGrpc request,
        io.grpc.stub.StreamObserver<bookstore.OrderResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service BookstoreService.
   */
  public static final class BookstoreServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<BookstoreServiceBlockingStub> {
    private BookstoreServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BookstoreServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BookstoreServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public bookstore.UserResponse getUser(bookstore.UserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public bookstore.BookList getBooks(bookstore.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBooksMethod(), getCallOptions(), request);
    }

    /**
     */
    public bookstore.OrderResponse getOrder(bookstore.OrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrderMethod(), getCallOptions(), request);
    }

    /**
     */
    public bookstore.OrderResponse createOrder(bookstore.CreateOrderRequestGrpc request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service BookstoreService.
   */
  public static final class BookstoreServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<BookstoreServiceFutureStub> {
    private BookstoreServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BookstoreServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BookstoreServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bookstore.UserResponse> getUser(
        bookstore.UserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bookstore.BookList> getBooks(
        bookstore.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetBooksMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bookstore.OrderResponse> getOrder(
        bookstore.OrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrderMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bookstore.OrderResponse> createOrder(
        bookstore.CreateOrderRequestGrpc request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER = 0;
  private static final int METHODID_GET_BOOKS = 1;
  private static final int METHODID_GET_ORDER = 2;
  private static final int METHODID_CREATE_ORDER = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_USER:
          serviceImpl.getUser((bookstore.UserRequest) request,
              (io.grpc.stub.StreamObserver<bookstore.UserResponse>) responseObserver);
          break;
        case METHODID_GET_BOOKS:
          serviceImpl.getBooks((bookstore.Empty) request,
              (io.grpc.stub.StreamObserver<bookstore.BookList>) responseObserver);
          break;
        case METHODID_GET_ORDER:
          serviceImpl.getOrder((bookstore.OrderRequest) request,
              (io.grpc.stub.StreamObserver<bookstore.OrderResponse>) responseObserver);
          break;
        case METHODID_CREATE_ORDER:
          serviceImpl.createOrder((bookstore.CreateOrderRequestGrpc) request,
              (io.grpc.stub.StreamObserver<bookstore.OrderResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              bookstore.UserRequest,
              bookstore.UserResponse>(
                service, METHODID_GET_USER)))
        .addMethod(
          getGetBooksMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              bookstore.Empty,
              bookstore.BookList>(
                service, METHODID_GET_BOOKS)))
        .addMethod(
          getGetOrderMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              bookstore.OrderRequest,
              bookstore.OrderResponse>(
                service, METHODID_GET_ORDER)))
        .addMethod(
          getCreateOrderMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              bookstore.CreateOrderRequestGrpc,
              bookstore.OrderResponse>(
                service, METHODID_CREATE_ORDER)))
        .build();
  }

  private static abstract class BookstoreServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BookstoreServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return bookstore.Bookstore.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BookstoreService");
    }
  }

  private static final class BookstoreServiceFileDescriptorSupplier
      extends BookstoreServiceBaseDescriptorSupplier {
    BookstoreServiceFileDescriptorSupplier() {}
  }

  private static final class BookstoreServiceMethodDescriptorSupplier
      extends BookstoreServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    BookstoreServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BookstoreServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BookstoreServiceFileDescriptorSupplier())
              .addMethod(getGetUserMethod())
              .addMethod(getGetBooksMethod())
              .addMethod(getGetOrderMethod())
              .addMethod(getCreateOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
