package com.example.grpc.service;

import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.example.grpc.HelloServiceGrpc.HelloServiceImplBase;
import com.example.grpc.PersistRequest;
import com.example.grpc.PersistResponse;
import com.example.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloService extends HelloServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloService.class);

  private final PersistenceServiceBlockingStub persistenceService;

  public HelloService(PersistenceServiceBlockingStub persistenceService) {
    this.persistenceService = persistenceService;
  }

  @Override
  public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
    Thread thread = Thread.currentThread();
    LOGGER.info("[{}]: Received request from {}", thread, request.getName());
    PersistResponse persistResponse =
        persistenceService.persist(PersistRequest.newBuilder().setName(request.getName()).build());
    String message = String.format("Hello %s (%s)", request.getName(), persistResponse.getCount());
    HelloResponse response = HelloResponse.newBuilder().setMessage(message).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
    LOGGER.info("[{}] Sending response for {} ({})", thread, request.getName(), persistResponse.getCount());
  }
}
