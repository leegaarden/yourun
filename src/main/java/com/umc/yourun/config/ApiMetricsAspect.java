package com.umc.yourun.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ApiMetricsAspect {
    private final MeterRegistry meterRegistry;

    public ApiMetricsAspect(MeterRegistry registry) {
        this.meterRegistry = registry;
    }

    @Before("execution(* com.umc.yourun.controller..*.*(..))")
    public void countApiCall(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String path = request.getRequestURI();

        // Challenges API 모니터링
        if (path.startsWith("/api/v1/challenges/")) {
            String type = path.contains("/crew") ? "crew" :
                    path.contains("/solo") ? "solo" : "other";

            String endpoint = path.substring("/api/v1/challenges/".length());

            Counter.builder("api.calls")
                    .tag("category", "challenges")
                    .tag("type", type)
                    .tag("endpoint", endpoint)
                    .description("Number of Challenge API calls")
                    .register(meterRegistry)
                    .increment();
        }

        // Users API 모니터링
        else if (path.startsWith("/api/v1/users/")) {
            String type;
            if (path.contains("/mate")) {
                type = "mate";
            } else if (path.contains("/runnings")) {
                type = "runnings";
            } else {
                type = "users";
            }

            String endpoint = path.substring("/api/v1/users/".length());

            Counter.builder("api.calls")
                    .tag("category", "users")
                    .tag("type", type)
                    .tag("endpoint", endpoint)
                    .description("Number of Users API calls")
                    .register(meterRegistry)
                    .increment();
        }
    }
}