package com.sammy.controller;

import com.sammy.entity.api.CakeApiDTO;
import com.sammy.service.CakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WithMockUser
@WebFluxTest(controllers = CakeController.class)
class CakeControllerTest {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private CakeService cakeService;

    Flux<CakeApiDTO> apiCakes;

    @BeforeEach
    void setupCakeData() {
        apiCakes = Flux.fromIterable(setupCakes());
    }

    @Test
    void shouldCreateCakes() {

        given(cakeService.createCake(apiCakes)).willReturn(Mono.empty());

        testClient.mutateWith(csrf())
                  .post().uri("/cakes")
                  .exchange().expectStatus().isCreated();
    }

    @Test
    void shouldShowAllCakes() {

        given(cakeService.showAllCakes()).willReturn(apiCakes);

        Flux<CakeApiDTO> exchangeResult = testClient.get()
                                                    .uri("/").exchange()
                                                    .expectStatus().isOk()
                                                    .returnResult(CakeApiDTO.class)
                                                    .getResponseBody();

        StepVerifier.create(exchangeResult)
                    .recordWith(ArrayList::new)
                    .expectNextCount(2)
                    .consumeRecordedWith(apiCakes -> {
                        assertThat(apiCakes).hasSize(2);
                        assertThat(apiCakes).extracting(CakeApiDTO::getDesc)
                                            .contains("Beautiful materials", "More beautiful materials");
                    }).verifyComplete();

        verify(cakeService).showAllCakes();
        verifyNoMoreInteractions(cakeService);
    }

    @Test
    void shouldDownloadJSONFile() {
        given(cakeService.showAllCakes()).willReturn(apiCakes);

        testClient.get().uri("/cakes")
                  .exchange()
                  .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                  .expectStatus().isOk();
    }

    private List<CakeApiDTO> setupCakes() {
        CakeApiDTO firstApiCake = CakeApiDTO.builder()
                                            .cakeId(1L)
                                            .title("Some cake")
                                            .desc("Beautiful materials")
                                            .image("From a link")
                                            .build();

        CakeApiDTO secondApiCake = CakeApiDTO.builder()
                                             .cakeId(2L)
                                             .title("Some other cake")
                                             .desc("More beautiful materials")
                                             .image("From another link")
                                             .build();

        return List.of(firstApiCake, secondApiCake);
    }
}