package com.umc.yourun.service;
import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.converter.UserMateConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;
import com.umc.yourun.dto.user.UserResponseDTO;
import com.umc.yourun.repository.UserMateRepository;
import com.umc.yourun.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    
    public List<UserResponseDTO.userMateInfo> getUserMates(String token){
        User user = jwtTokenProvider.getUserByToken(token);
        List<UserMate> userMates = user.getUserMates();

        List<UserResponseDTO.userMateInfo> userMateInfos = new ArrayList<>();
        for(UserMate userMate : userMates){
            userMateInfos.add(UserConverter.toUserMateInfo(userMate.getMate()));
        }

        return userMateInfos;
    }

    public Boolean deleteMate(String token, Long mateId){
        User user = jwtTokenProvider.getUserByToken(token);
        Optional<User> mate = userRepository.findById(mateId);
        UserMate userMate = userMateRepository.findByUserAndMate(user,mate.get());
        userMateRepository.delete(userMate);
        return true;
    }
}
