package com.example.grpc.service;

import com.example.grpc.PersistRequest;
import com.example.grpc.PersistResponse;
import com.example.grpc.PersistenceServiceGrpc.PersistenceServiceImplBase;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceService extends PersistenceServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);
  private final ConcurrentHashMap<String, AtomicInteger> data;

  public PersistenceService() {
    data = new ConcurrentHashMap<>();
  }

  @Override
  public void persist(PersistRequest request, StreamObserver<PersistResponse> responseObserver) {
    Thread thread = Thread.currentThread();
    LOGGER.info("[{}]: Received request from {}", thread, request.getName());
    AtomicInteger counter = data.computeIfAbsent(request.getName(), (key) -> new AtomicInteger(0));
    try {
      Thread.sleep(1000);
      int count = counter.incrementAndGet();
      responseObserver.onNext(PersistResponse.newBuilder().setCount(count).build());
      responseObserver.onCompleted();
      LOGGER.info("[{}] Sending response for {} ({})", thread, request.getName(), count);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
