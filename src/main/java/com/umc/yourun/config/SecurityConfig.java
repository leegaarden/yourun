package com.umc.yourun.config;

import com.umc.yourun.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(requests -> requests
                        // Actuator 엔드포인트를 가장 먼저, 가장 광범위하게 permitAll()으로 설정
                        .requestMatchers(
                                "/actuator/**",
                                "/actuator/prometheus"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/api/v1/users/login",
                                "/api/v1/users",
                                "/api/v1/users/duplicate",
                                "/api/v1/users/check-nickname",
                                "/api/v1/oauth2/**",
                                "/login/oauth2/**",
                                "/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/api/v1/users/kakao-login", true)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/actuator/**",
                                "/actuator/prometheus"
                        ).permitAll()
                        .requestMatchers("/oauth/token").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/oauth/token", "/actuator/**")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(@Value("${springdoc.security.oauth2.client.registration.custom-client.token-uri}") String tokenUri) {
        return AuthorizationServerSettings.builder()
                .issuer(tokenUri)
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