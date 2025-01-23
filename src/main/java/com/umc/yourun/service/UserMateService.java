package com.umc.yourun.service;
import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.UserMateConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.repository.UserMateRepository;
import com.umc.yourun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserMateService {
    private UserMateRepository userMateRepository;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserMateService(UserMateRepository userMateRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userMateRepository = userMateRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Boolean addmate(String token,Long mateId){
        User user = jwtTokenProvider.getUserByToken(token);
        Optional<User> mate = userRepository.findById(mateId);
        if(mate.isEmpty()){
            return false;
        }
        userMateRepository.save(UserMateConverter.toUserMate(user, mate.get()));
        return true;
    }
}
