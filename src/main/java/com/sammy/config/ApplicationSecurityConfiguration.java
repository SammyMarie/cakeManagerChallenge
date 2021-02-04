package com.sammy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class ApplicationSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf().disable().authorizeExchange().anyExchange().permitAll();
        return httpSecurity.build();
    }
}