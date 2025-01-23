package com.umc.yourun.service;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.converter.UserTagConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.UserStatus;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.repository.UserTagRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.umc.yourun.config.exception.custom.UserException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, UserTagRepository userTagRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userTagRepository = userTagRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean joinMember(UserRequestDTO.JoinDto request) {
        if(!request.password().equals(request.passwordcheck())){
            return false;
        }
        if(request.tag1().equals(request.tag2())){
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

        Map<String, String> token = new HashMap<>();

        token.put("access_token", jwtTokenProvider.createToken(loginDto.email(), Collections.singletonList("USER")));
        return token;
    }

    public Boolean deleteUser(String accessToken){
        User user = jwtTokenProvider.getUserByToken(accessToken);
        user.setStatus(UserStatus.valueOf("INACTIVE"));
        user.setInactive_date(LocalDateTime.now());
        return true;
    }
}
