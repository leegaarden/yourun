package com.umc.yourun.service;

import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.converter.UserTagConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.repository.UserTagRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.umc.yourun.config.exception.custom.UserException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Autowired
    public UserService(UserRepository userRepository, UserTagRepository userTagRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userTagRepository = userTagRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public boolean joinMember(UserRequestDTO.JoinDto request) {
        if(!request.password().equals(request.passwordcheck())){
            return false;
        }

        User newUser = UserConverter.toMember(request);

        newUser.encodePassword(passwordEncoder.encode(request.password()));

        userRepository.save(newUser);
        userTagRepository.save(UserTagConverter.toUserTag(newUser, request.tag1()));
        userTagRepository.save(UserTagConverter.toUserTag(newUser, request.tag2()));

        return true;
    }

    public Map<String,String> login(UserRequestDTO.LoginDto loginDto) throws UserException {
        // 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );

        // 인증 컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 생성 헤더
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        // 클레임 (Payload)
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", loginDto.email());
        //claims.put("role", "USER");

        Map<String, String> token = new HashMap<>();

        // 현재 시간
        long now = System.currentTimeMillis();

        token.put("access_token", Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setSubject("AccessToken") // 토큰 용도
                .setIssuedAt(new Date(now)) // 발행 시간
                .setExpiration(new Date(now + 1000 * 60 * 15)) // 만료 시간
                .signWith(SECRET_KEY) // 서명
                .compact());
        return token;
    }

    public Optional<User> getUserByToken(String token){
        Claims claims;
        // 토큰 검증 및 Claims 추출
        try {
            claims = Jwts.parserBuilder()
                            .setSigningKey(SECRET_KEY) // 비밀 키 설정
                            .build()
                            .parseClaimsJws(token) // 토큰 검증
                            .getBody(); // Claims 반환
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
        return userRepository.findByEmail((String) claims.get("email"));
    }
}
