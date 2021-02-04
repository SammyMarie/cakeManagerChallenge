package com.sammy.service;

import com.sammy.entity.api.CakeApiDTO;
import com.sammy.entity.business.CakeDTO;
import com.sammy.entity.mapper.CakeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static com.sammy.entity.mapper.CakeMapper.toApi;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class CakeServiceImplTest {

    @Autowired
    private CakeServiceImpl cakeService;

    @BeforeEach
    void setupTestData() {
        saveCakes();
    }

    @Test
    @Order(1)
    void shouldDisplayCake() {
        Mono<CakeDTO> cake = cakeService.showCake(1L).map(CakeMapper::toBusiness);

        StepVerifier.create(cake)
                    .assertNext(cakeDTO -> {
                        assertThat(cakeDTO.getCakeId()).isEqualTo(1L);
                        assertThat(cakeDTO.getDesc()).isEqualTo("This cake has apricot in it.");
                    }).verifyComplete();
    }

    @Test
    @Order(2)
    void shouldDisplayAllCakes() {
        Flux<CakeDTO> businessCakes = cakeService.showAllCakes()
                                                 .map(CakeMapper::toBusiness);

        StepVerifier.create(businessCakes)
                    .recordWith(ArrayList::new)
                    .expectNextCount(3L)
                    .consumeRecordedWith(result -> {
                        assertThat(result).hasSize(3);
                        assertThat(result).extracting(CakeDTO::getCakeId).contains(1L, 2L, 3L);
                    }).expectComplete().verify();
    }

    @Test
    @Order(3)
    void shouldUpdateCake() {
        CakeDTO cakeDTO = CakeDTO.builder()
                                 .cakeId(1L)
                                 .title("Smug apricot cake")
                                 .desc("This cake has apricot in it.")
                                 .image("http link")
                                 .build();

        Mono<CakeDTO> cakeUpdated = cakeService.updateCake(Mono.fromSupplier(() -> cakeDTO).map(CakeMapper::toApi))
                                               .map(CakeMapper::toBusiness);

        StepVerifier.create(cakeUpdated)
                    .assertNext(cake -> {
                        assertThat(cake.getCakeId()).isEqualTo(1L);
                        assertThat(cake.getTitle()).isEqualTo("Smug apricot cake");
                    }).verifyComplete();
    }

    @Test
    @Order(4)
    void shouldDeleteCake() {
        Mono<Void> deletedCake = cakeService.deleteCake(1L);

        StepVerifier.create(deletedCake.log()).verifyComplete();
    }

    private List<CakeDTO> buildBusinessCake() {
        CakeDTO firstCake = CakeDTO.builder()
                                   .cakeId(1L)
                                   .title("Apricot smug cake")
                                   .desc("This cake has apricot in it.")
                                   .image("http link")
                                   .build();

        CakeDTO secondCake = CakeDTO.builder()
                                    .cakeId(2L)
                                    .title("Bamboo Sprot cake")
                                    .desc("Made from bamboo spruts")
                                    .image("https link")
                                    .build();

        CakeDTO thirdCake = CakeDTO.builder()
                                   .cakeId(3L)
                                   .title("Banana cake")
                                   .desc("Made from over-ripe bananas")
                                   .image("https link")
                                   .build();

        return List.of(firstCake, secondCake, thirdCake);
    }

    private void saveCakes() {
        List<CakeApiDTO> cakesList = new ArrayList<>();
        buildBusinessCake().forEach(cake -> cakesList.add(toApi(cake)));
        cakeService.createCake(Flux.fromIterable(cakesList)).subscribe();
    }
}