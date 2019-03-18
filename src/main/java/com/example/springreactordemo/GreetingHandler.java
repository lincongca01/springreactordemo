package com.example.springreactordemo;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {
  private static final String RESPONSE_STRING_FORMAT = "Hello from '%s': %d";
  private AtomicInteger incrementer = new AtomicInteger (0);

  public Mono<ServerResponse> hello(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
      .body(BodyInserters.fromObject(
        String.format(
          RESPONSE_STRING_FORMAT,
          System.getenv().getOrDefault("HOSTNAME", "unknown"),
          incrementer.incrementAndGet())
      ));
  }
}