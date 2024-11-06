package com.fiuni.distri.project.fiuni.filter;

import com.fiuni.distri.project.fiuni.config.RouteValidator;
import com.fiuni.distri.project.fiuni.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(1)
public class AuthFilter implements GlobalFilter {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private RouteValidator routeValidator;

    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // GET PATH AND VERIFY IF IT IS A SECURED ONE
        String path = exchange.getRequest().getURI().getPath();
        if (!routeValidator.isSecured(path)) return chain.filter(exchange);

        // GET THE AUTH HEADER
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // CHECK IF THE TOKEN EXISTS IN THE AUTH HEADER
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        // EXTRACTION OF THE JWT
        String token = authHeader.substring(7);

        // VERIFICATION OF TOKEN
        try {
            if (!jwtUtils.validateAccessToken(token)) {
                return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        } catch (Exception e) {
            return buildErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during token validation");
        }

        // ADD TOKEN TO THE REQUEST HEADERS FOR FORWARDING
        exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, authHeader) // Re-add the Authorization header
                .build();

        // CONTINUE INTO THE OTHER FILTERS / INTERNAL SERVICES
        return chain.filter(exchange);
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String jsonResponse = String.format(
                "{ \"httpStatus\": %d, \"success\": false, \"message\": \"%s\", \"timeStamp\": \"%s\", \"data\": null }",
                status.value(), message, java.time.LocalDateTime.now().toString()
        );
        DataBuffer buffer = bufferFactory.wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}

