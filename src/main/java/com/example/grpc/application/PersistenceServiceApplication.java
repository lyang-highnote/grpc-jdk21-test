package com.example.grpc.application;

import com.example.grpc.service.PersistenceService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceServiceApplication {

  public static final int PORT = 9999;
  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceServiceApplication.class);

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server =
        ServerBuilder.forPort(PORT)
            .addService(new PersistenceService())
            .addService(ProtoReflectionService.newInstance())
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .build();
    server.start();
    LOGGER.info("Server started, listening on " + server.getPort());
    server.awaitTermination();
  }
}
