package com.example.grpc.application;

import com.example.grpc.PersistenceServiceGrpc;
import com.example.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import com.example.grpc.service.HelloService;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceDefaultExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceDefaultExecutor.class);
  public static final int PORT = 9901;

  public static void main(String[] args) throws IOException, InterruptedException {
    PersistenceServiceBlockingStub persistenceService = getPersistenceServiceBlockingStub();
    Server server =
        ServerBuilder.forPort(PORT)
            .addService(new HelloService(persistenceService))
            .addService(ProtoReflectionService.newInstance())
            .build();
    server.start();
    LOGGER.info("Server started, listening on " + server.getPort());
    server.awaitTermination();
  }

  private static PersistenceServiceBlockingStub getPersistenceServiceBlockingStub() {
    return PersistenceServiceGrpc.newBlockingStub(
        ManagedChannelBuilder.forAddress("localhost", PersistenceServiceApplication.PORT).usePlaintext().build());
  }
}
