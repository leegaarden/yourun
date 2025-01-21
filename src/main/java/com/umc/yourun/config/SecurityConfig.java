package com.umc.yourun.config;

import com.umc.yourun.domain.User;
import com.umc.yourun.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.Provider;
import java.util.Collection;
import java.util.UUID;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/users/**")
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/users/login", "/users").permitAll()
                      //.requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Spring Authorization Server 기본 설정 적용
        //OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                .securityMatcher("/oauth/**") // /oauth/** 경로만 처리
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth/token").permitAll() // /oauth/token은 공개
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth/token")); // CSRF 비활성화

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(@Value("${springdoc.security.oauth2.client.registration.custom-client.token-uri}") String tokenUri){
        return AuthorizationServerSettings.builder()
                .issuer(tokenUri) // 인증 서버의 기본 주소
                .tokenEndpoint("/oauth/token")
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
