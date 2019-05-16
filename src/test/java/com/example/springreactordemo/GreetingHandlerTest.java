package com.example.springreactordemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GreetingHandlerTest {
  private final String uri = "/api/hello";
  @Autowired
  WebTestClient testClient;

  @Before
  public void setUp() {
  }

  @Test
  public void helloworld() throws Exception {
    testClient.get().uri(uri)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(res -> {
          assertTrue(new String(res.getResponseBody()).endsWith("1"));
    });
    testClient.get().uri(uri)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(res -> {
        assertTrue(new String(res.getResponseBody()).endsWith("2"));
    });
  }

  @Test
  public void helloworld_concurrent() throws Exception {
    final int numOfReqests = 100;

    Set<String> result = Flux.range(1, numOfReqests)
      .parallel()
      .runOn(Schedulers.elastic())
      .map(i -> {
          return new String(testClient.get().uri(uri)
              .exchange()
              .expectStatus().isOk()
              .returnResult(String.class)
              .getResponseBodyContent());
          })
      .sequential()
      .collect(Collectors.toSet())
      .block(Duration.ofSeconds(20));

    assertEquals("Number of non-duplicate results should be the same as number of requests.", 
        numOfReqests, result.size());
  }

}


