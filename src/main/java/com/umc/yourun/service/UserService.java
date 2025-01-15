package com.umc.yourun.service;

import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.umc.yourun.config.exception.ErrorCode.INVALID_USER_INCONSISTENCY;
import static com.umc.yourun.config.exception.ErrorCode.INVALID_USER_NOTFOUND;
import com.umc.yourun.config.exception.custom.UserException;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean joinMember(UserRequestDTO.JoinDto request) {
        if(!request.password().equals(request.passwordcheck())){
            return false;
        }

        User newUser = UserConverter.toMember(request);

        newUser.encodePassword(passwordEncoder.encode(request.password()));

        userRepository.save(newUser);

        return true;
    }

    public Boolean login(UserRequestDTO.LoginDto loginDto) throws UserException {
        User user = userRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> new UserException(INVALID_USER_NOTFOUND));

        if (passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            return true;
        }else{
            return false;
        }
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getNickname())
//                .password(user.getPassword())
//                .build();
    }
}
