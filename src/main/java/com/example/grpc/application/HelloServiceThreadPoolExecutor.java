package com.example.grpc.application;

import com.example.grpc.PersistenceServiceGrpc;
import com.example.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import com.example.grpc.service.HelloService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceThreadPoolExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceThreadPoolExecutor.class);
  public static final int POOL_SIZE = 200;
  public static final int KEEP_ALIVE_TIME = 60;
  public static final int PORT = 9902;

  public static void main(String[] args) throws IOException, InterruptedException {
    PersistenceServiceBlockingStub persistenceService = getPersistenceServiceBlockingStub();

    Server server =
        ServerBuilder.forPort(PORT)
            .addService(new HelloService(persistenceService))
            .addService(ProtoReflectionService.newInstance())
            .executor(getExecutor())
            .build();
    server.start();
    LOGGER.info("Server started, listening on " + server.getPort());
    server.awaitTermination();
  }

  private static PersistenceServiceBlockingStub getPersistenceServiceBlockingStub() {
    return PersistenceServiceGrpc.newBlockingStub(
        ManagedChannelBuilder.forAddress("localhost", PersistenceServiceApplication.PORT).usePlaintext().build());
  }

  private static ThreadPoolExecutor getExecutor() {
    ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(POOL_SIZE);
    ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("grpc-server-%d").build();
    RejectedExecutionHandler handler = HelloServiceThreadPoolExecutor::rejected;
    return new ThreadPoolExecutor(
        POOL_SIZE, POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory, handler);
  }

  private static void rejected(Runnable runnable, ThreadPoolExecutor executor) {
    LOGGER.warn("request rejected from executor.");
  }
}
