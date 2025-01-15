package com.umc.yourun.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class TokenService {
    private final WebClient webClient;

    public TokenService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public String getAccessToken(String username, String password) {
        ResponseEntity<Map> response = webClient.post()
                .uri("/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=password&username=" + username + "&password=" + password +
                        "&client_id=your-client-id&client_secret=your-client-secret")
                .retrieve()
                .toEntity(Map.class)
                .block();

        if (response != null && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }
        throw new RuntimeException("Failed to obtain access token");
    }
}
