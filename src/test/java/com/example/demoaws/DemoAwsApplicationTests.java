package com.example.demoaws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@FunctionalSpringBootTest
@AutoConfigureWebTestClient
class DemoAwsApplicationTests {

    @Autowired
    private WebTestClient client;

    @Test
    void testRequest() {
        Assertions.assertTrue(client.post().uri("/function").body(Mono.just("test"), String.class).exchange()
                .expectStatus().isOk().expectBody(String.class).returnResult().getResponseBody()
                .matches("[-+]?\\d+"));
    }

}
