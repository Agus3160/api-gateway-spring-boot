package com.fiuni.distri.project.fiuni.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // AUTH MODULE
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("http://localhost:9090"))

                // JOSE AGUSTIN OVIEDO VILLALBA MODULES
                .route("service-a", r -> r.path("/user/**", "/role/**", "empleado/**")
                        .uri("http://localhost:9091"))

                // ANNIA MICAELA BENITEZ HODBAUER MODULES

                // WILLIAM MARTINEZ MODULES

                .build();
    }
}
