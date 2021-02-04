package com.sammy.service;

import com.sammy.entity.api.CakeApiDTO;
import com.sammy.entity.mapper.CakeMapper;
import com.sammy.repository.CakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.sammy.entity.mapper.CakeMapper.toApi;

@Service
@RequiredArgsConstructor
public class CakeServiceImpl implements CakeService {

    private final CakeRepository cakeRepository;

    @Override
    public Mono<CakeApiDTO> createCake(Flux<CakeApiDTO> cakes) {
        return cakes.map(CakeMapper::toBusiness)
                    .map(cakeRepository::save)
                    .log("Saving cake")
                    .map(CakeMapper::toApi)
                    .last();
    }

    @Override
    public Mono<CakeApiDTO> showCake(long cakeId) {
        return Mono.fromSupplier(() -> toApi(cakeRepository.findById(cakeId)
                                                           .orElseThrow()))
                   .onErrorResume(result -> Mono.error(new IllegalArgumentException("Wrong cakeId provided " + cakeId)));
    }

    @Override
    public Flux<CakeApiDTO> showAllCakes() {
        return Flux.fromIterable(cakeRepository.findAll())
                   .map(CakeMapper::toApi);
    }

    @Override
    public Mono<CakeApiDTO> updateCake(Mono<CakeApiDTO> apiCake) {
        return apiCake.map(CakeMapper::toBusiness)
                      .map(cakeRepository::save)
                      .onErrorResume(result -> Mono.error(new IllegalArgumentException("Cake data not found ")))
                      .map(CakeMapper::toApi);
    }

    @Override
    public Mono<Void> deleteCake(long cakeId) {
        Mono<Void> deletePicture = Mono.fromSupplier(() -> {
            cakeRepository.findById(cakeId).ifPresent(cakeRepository::delete);
            return null;
        });
        return Mono.when(deletePicture).log("deleted cake");
    }
}