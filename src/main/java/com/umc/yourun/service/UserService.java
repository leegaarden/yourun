package com.umc.yourun.service;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.converter.UserTagConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.UserStatus;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.repository.UserTagRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
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
        if(!checkUserNickname(request.nickname())){
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

    public Map<String, String> kakaoLogin(OAuth2User user) {
        Map<String, String> token = new HashMap<>();
        token.put("access_token", jwtTokenProvider.createToken(user.getAttribute("email"), Collections.singletonList("USER")));
        return token;
    }

    public Boolean setKakaoUserInfo(String accessToken, UserRequestDTO.SetKakaoUserDto kakaoUserInfo) {
        if (!userRepository.findByNickname(kakaoUserInfo.nickname()).isEmpty()) {
            throw new ValidationException("이미 사용중인 닉네임입니다.");
        }
        if (kakaoUserInfo.tag1().equals(kakaoUserInfo.tag2())) {
            throw new ValidationException("같은 테그를 선택할 수 없습니다.");
        }

        User user = jwtTokenProvider.getUserByToken(accessToken);

        userRepository.save(user.setKakaoUser(kakaoUserInfo));
        userTagRepository.save(UserTagConverter.toUserTag(user, kakaoUserInfo.tag1()));
        userTagRepository.save(UserTagConverter.toUserTag(user, kakaoUserInfo.tag2()));

        return true;
    }

    public User getUserInfo(String accessToken) {
        return jwtTokenProvider.getUserByToken(accessToken);
    }

    public User updateUserInfo(String accessToken, UserRequestDTO.UpdateDto request) {
        User user = jwtTokenProvider.getUserByToken(accessToken);
        if(!checkUserNickname(request.nickname())&& !user.getNickname().equals(request.nickname())){
            throw new ValidationException("이미 사용중인 닉네임입니다.");
        }
        if (request.tag1().equals(request.tag2())) {
            throw new ValidationException("같은 테그를 선택할 수 없습니다.");
        }
        //만약 이전 내용과 동일하다면 업데이트 하지 않음
        if(user.getNickname().equals(request.nickname()) && user.getUserTags().get(0).getTag().equals(request.tag1()) && user.getUserTags().get(1).getTag().equals(request.tag2())){
            return user;
        }
        user.updateNickname(request.nickname());
        userTagRepository.deleteAllByUser(user);
        userTagRepository.save(UserTagConverter.toUserTag(user, request.tag1()));
        userTagRepository.save(UserTagConverter.toUserTag(user, request.tag2()));
        return user;
    }

    public Boolean deleteUser(String accessToken) {
        User user = jwtTokenProvider.getUserByToken(accessToken);
        user.setStatus(UserStatus.valueOf("INACTIVE"));
        user.setInactive_date(LocalDateTime.now());
        return true;
    }

    public Boolean duplicateUserCheck(String email) {
		return userRepository.findByEmail(email).isEmpty();
    }

    public Boolean checkUserNickname(String nickName) {
        return userRepository.findByNickname(nickName).isEmpty();
    }
}
