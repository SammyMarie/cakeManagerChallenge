package com.sammy.entity;

import com.sammy.entity.api.CakeApiDTO;
import com.sammy.entity.business.CakeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sammy.entity.mapper.CakeMapper.toApi;
import static com.sammy.entity.mapper.CakeMapper.toBusiness;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CakeMapperTest {

    private CakeApiDTO apiCake;
    private CakeDTO businessCake;

    @BeforeEach
    void setup() {
        apiCake = CakeApiDTO.builder()
                            .cakeId(1L)
                            .title("Apricot smug cake")
                            .desc("This cake has apricot in it.")
                            .image("http link")
                            .build();

        businessCake = CakeDTO.builder()
                              .cakeId(2L)
                              .title("Bamboo Sprot cake")
                              .desc("Made from bamboo spruts")
                              .image("https link")
                              .build();
    }

    @Test
    void shouldConvertToApiCake() {
        assertThat(businessCake).usingRecursiveComparison().isEqualTo(toApi(businessCake));
    }

    @Test
    void shouldConvertToBusinessCake() {
        assertThat(apiCake).usingRecursiveComparison().isEqualTo(toBusiness(apiCake));
    }
}