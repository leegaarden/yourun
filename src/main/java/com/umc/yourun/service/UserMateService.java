package com.umc.yourun.service;
import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.converter.UserMateConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.dto.user.UserResponseDTO;
import com.umc.yourun.repository.RunningDataRepository;
import com.umc.yourun.repository.UserMateRepository;
import com.umc.yourun.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserMateService {
    private UserMateRepository userMateRepository;
    private UserRepository userRepository;
    private RunningDataRepository runningDataRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserMateService(UserMateRepository userMateRepository, UserRepository userRepository, RunningDataRepository runningDataRepository, JwtTokenProvider jwtTokenProvider) {
        this.userMateRepository = userMateRepository;
        this.userRepository = userRepository;
        this.runningDataRepository = runningDataRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Boolean addmate(String token, Long mateId) throws ValidationException{
        User user = jwtTokenProvider.getUserByToken(token);
        Optional<User> mate = userRepository.findById(mateId);

        if(mate.isEmpty()){
            throw new ValidationException("존재하지 않는 유저를 메이트로 추가할 수 없습니다.");
        }
        if(mate.get().getId().equals(user.getId())){
            throw new ValidationException("자기 자신을 메이트로 추가할 수 없습니다.");
        }
        for(UserMate userMate : user.getUserMates()){
            if(userMate.getMate().getId().equals(mateId)){
                throw new ValidationException("이미 추가한 메이트를 중복으로 추가할 수 없습니다.");
            }
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
            User mate = userMate.getMate();
            userMateInfos.add(UserConverter.toUserMateInfo(mate, runningDataRepository.sumDistanceByUserId(mate.getId()), calculateCountDay(mate.getCreatedAt().toLocalDate())));
        }

        return userMateInfos;
    }

    public Boolean deleteMate(String token, Long mateId) throws ValidationException{
        User user = jwtTokenProvider.getUserByToken(token);
        Optional<User> mate = userRepository.findById(mateId);

        Boolean check = false;
        for(UserMate userMate : user.getUserMates()){
            if(userMate.getMate().getId().equals(mateId)){
                check = true;
            }
        }

        if(check) {
            UserMate userMate = userMateRepository.findByUserAndMate(user, mate.get());
            userMateRepository.delete(userMate);
        }else {
            throw new ValidationException("메이트 목록에 존재하지 않는 메이트는 삭제할 수 없습니다.");
        }

        return true;
    }

    public List<UserResponseDTO.userMateInfo> recommendFiveMates(String token) throws ValidationException{
        User user = jwtTokenProvider.getUserByToken(token);

        List<Long> excludeIdList = new ArrayList<>();
        excludeIdList.add(user.getId());
        for(UserMate userMate :user.getUserMates()){
            excludeIdList.add(userMate.getMate().getId());
        }

        List<String> userTags = new ArrayList<>();
        for(UserTag userTag : user.getUserTags()){
            userTags.add(userTag.getTag().toString());
        }

        List<User> users = userRepository.findRandomFive(excludeIdList, userTags);
        if(users.isEmpty()){
            throw new ValidationException("메이트로 추천할 수 있는 유저가 0명이므로 추천에 실패했습니다.");
        }

        List<UserResponseDTO.userMateInfo> userMateInfos = new ArrayList<>();
        for(User temp : users){
            userMateInfos.add(UserConverter.toUserMateInfo(temp, runningDataRepository.sumDistanceByUserId(temp.getId()), calculateCountDay(temp.getCreatedAt().toLocalDate())));
        }

        return userMateInfos;
    }

    private int calculateCountDay(LocalDate startDate) {
        return (int) ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
    }
}
