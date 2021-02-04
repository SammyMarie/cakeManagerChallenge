package com.sammy.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sammy.entity.api.CakeApiDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.util.MimeTypeUtils.parseMimeType;

@WithMockUser
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CakeControllerIntegrationTest {

    @Autowired
    private WebTestClient testClient;

    @BeforeEach
    void setupCakeData() {

        testClient.post()
                  .uri("/cakes")
                  .body(loadCakesFromAPI(), CakeApiDTO.class)
                  .exchange()
                  .expectStatus().isCreated();
    }

    @Test
    void shouldShowCake() {
        testClient.get().uri("/cakes".concat("/{cakeId}"), "1")
                  .exchange()
                  .expectBody()
                  .jsonPath("$.title").isEqualTo("Lemon cheesecake");
    }

    @Test
    void shouldShowAllCakes() {

        Flux<CakeApiDTO> apiCakes = testClient.get().uri("/")
                                              .exchange()
                                              .expectStatus().isOk()
                                              .expectHeader().contentType(APPLICATION_JSON_VALUE)
                                              .returnResult(CakeApiDTO.class)
                                              .getResponseBody();

        StepVerifier.create(apiCakes)
                    .expectNextCount(20)
                    .verifyComplete();
    }

    private Flux<CakeApiDTO> loadCakesFromAPI() {

        return WebClient.builder()
                        .baseUrl("https://gist.githubusercontent.com")
                        .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodec).build())
                        .build()
                        .get()
                        .uri("/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json")
                        .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(CakeApiDTO.class));
    }

    private void acceptedCodec(ClientCodecConfigurer codecConfigurer) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        codecConfigurer.customCodecs()
                       .register(new Jackson2JsonDecoder(mapper, parseMimeType(TEXT_PLAIN_VALUE)));
    }
}