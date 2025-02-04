package com.umc.yourun.service;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.config.exception.GeneralExceptionHandler;
import com.umc.yourun.config.exception.custom.RunningException;
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
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.umc.yourun.config.exception.custom.UserException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    public boolean joinMember(UserRequestDTO.JoinDto request) throws ValidationException{
        if(!userRepository.findByEmail(request.email()).isEmpty()) {
            throw new ValidationException("이미 사용중인 이메일입니다.");
        }
        if (!request.password().equals(request.passwordcheck())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다");
        }
        if(!userRepository.findByNickname(request.nickname()).isEmpty()){
            throw new ValidationException("이미 사용중인 닉네임입니다.");
        }
        if (request.tag1().equals(request.tag2())) {
            throw new ValidationException("같은 테그를 선택할 수 없습니다.");
        }

        User newUser = UserConverter.toMember(request);

        newUser.encodePassword(passwordEncoder.encode(request.password()));

        userRepository.save(newUser);
        userTagRepository.save(UserTagConverter.toUserTag(newUser, request.tag1()));
        userTagRepository.save(UserTagConverter.toUserTag(newUser, request.tag2()));

        return true;
    }

    public Map<String, String> login(UserRequestDTO.LoginDto loginDto) throws ValidationException {
        if(userRepository.findByEmail(loginDto.email()).isEmpty()) {
            throw new ValidationException("존재하지 않은 이메일입니다.");
        }
        if(!passwordEncoder.matches(loginDto.password(),userRepository.findByEmail(loginDto.email()).get().getPassword())) {
            throw new ValidationException("비밀번호가 틀렸습니다.");
        }
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

    public Boolean deleteUser(String accessToken) {
        User user = jwtTokenProvider.getUserByToken(accessToken);
        user.setStatus(UserStatus.valueOf("INACTIVE"));
        user.setInactive_date(LocalDateTime.now());
        return true;
    }

    public Boolean duplicateUserCheck(String email) {
        if(userRepository.findByEmail(email).isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    public Boolean checkUserNickname(String nickName) {
        if(userRepository.findByNickname(nickName).isEmpty()){
            return true;
        }else{
            return false;
        }
    }
}