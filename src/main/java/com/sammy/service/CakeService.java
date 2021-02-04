package com.sammy.service;

import com.sammy.entity.api.CakeApiDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CakeService {
    Mono<CakeApiDTO> createCake(Flux<CakeApiDTO> cakes);

    Mono<CakeApiDTO> showCake(long cakeId);

    Flux<CakeApiDTO> showAllCakes();

    Mono<CakeApiDTO> updateCake(Mono<CakeApiDTO> apiCake);

    Mono<Void> deleteCake(long cakeId);
}