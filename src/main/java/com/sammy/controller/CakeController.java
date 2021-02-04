package com.sammy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sammy.entity.api.CakeApiDTO;
import com.sammy.service.CakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CakeController {

    private final CakeService cakeService;
    private final ObjectMapper mapper;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CakeApiDTO>>> displayAllCakes() {

        return cakeService.showAllCakes().collectList()
                          .map(cakes -> ResponseEntity.ok()
                                                      .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                                      .body(cakes));
    }

    @PostMapping("/cakes")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CakeApiDTO> createCake(@RequestBody Flux<CakeApiDTO> cakes) {
        return cakeService.createCake(cakes);
    }

    @GetMapping("/cakes")
    public Mono<ResponseEntity<InputStreamResource>> downloadJSONCakes() {
        return cakeService.showAllCakes().collectList()
                          .map(cakeApiDTOS -> {
                              try {
                                  return mapper.writeValueAsBytes(cakeApiDTOS);
                              } catch (JsonProcessingException e) {
                                  return new byte[0];
                              }
                          })
                          .map(bytes -> ResponseEntity.ok()
                                                      .contentLength(bytes.length)
                                                      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cakes.json\"")
                                                      .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                                      .body(new InputStreamResource(new ByteArrayInputStream(bytes))));
    }

    @GetMapping("/cakes/{cakeId}")
    public Mono<CakeApiDTO> showCake(@PathVariable long cakeId) {
        return cakeService.showCake(cakeId);
    }
}