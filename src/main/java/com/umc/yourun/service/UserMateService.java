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
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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

    public Boolean addmate(String token, Long mateId) throws ValidationException{
        if(userRepository.findById(mateId).isEmpty()){
            throw new ValidationException("존재하지 않는 유저를 메이트로 추가할 수 없습니다.");
        }
        User user = jwtTokenProvider.getUserByToken(token);
        Optional<User> mate = userRepository.findById(mateId);
        if(mate.isEmpty()){
            return false;
        }
        userMateRepository.save(UserMateConverter.toUserMate(user, mate.get()));
        return true;
    }
    
    public List<UserResponseDTO.userMateInfo> getUserMates(String token) throws ValidationException{
        User user = jwtTokenProvider.getUserByToken(token);
        List<UserMate> userMates = user.getUserMates();

        if(userMates.isEmpty()){
            throw new ValidationException("추가한 메이트가 0명입니다.");
        }

        List<UserResponseDTO.userMateInfo> userMateInfos = new ArrayList<>();
        for(UserMate userMate : userMates){
            userMateInfos.add(UserConverter.toUserMateInfo(userMate.getMate()));
        }

        return userMateInfos;
    }

    public Boolean deleteMate(String token, Long mateId) throws ValidationException{
        if(userRepository.findById(mateId).isEmpty()){
            throw new ValidationException("존재하지 않는 메이트를 삭제할 수 없습니다.");
        }
        User user = jwtTokenProvider.getUserByToken(token);
        Optional<User> mate = userRepository.findById(mateId);
        UserMate userMate = userMateRepository.findByUserAndMate(user,mate.get());
        userMateRepository.delete(userMate);
        return true;
    }

    public List<UserResponseDTO.userMateInfo> recommendFiveMates(String token){
        User user = jwtTokenProvider.getUserByToken(token);

        List<Long> excludeIdList = new ArrayList<>();
        excludeIdList.add(user.getId());
        for(UserMate userMate :user.getUserMates()){
            excludeIdList.add(userMate.getMate().getId());
        }

        List<User> users = userRepository.findRandomFive(excludeIdList);
        if(users.isEmpty()){
            throw new ValidationException("랜덤으로 추천할 수 있는 유저가 0명으로 추천에 실패했습니다.");
        }

        List<UserResponseDTO.userMateInfo> userMateInfos = new ArrayList<>();
        for(User temp : users){
            userMateInfos.add(UserConverter.toUserMateInfo(temp));
        }

        return userMateInfos;
    }
}
