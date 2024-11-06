package com.fiuni.distri.project.fiuni.config;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RouteValidator {
    private static final List<String> unprotectedEndpoints = List.of(
            "/auth/login",
            "/auth/signup"
    );
    public boolean isSecured(String path) {
        return unprotectedEndpoints.stream().noneMatch(path::startsWith);
    }
}
