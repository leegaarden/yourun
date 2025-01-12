package com.umc.yourun.service;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;
import com.umc.yourun.repository.UserMateRepository;
import com.umc.yourun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserMateService {
    UserMateRepository userMateRepository;
    UserRepository userRepository;

    @Autowired
    public UserMateService(UserMateRepository userMateRepository, UserRepository userRepository) {
        this.userMateRepository = userMateRepository;
        this.userRepository = userRepository;
    }

    public Boolean addmate(Long userId,Long mateId){
        Optional<User> tempUser = userRepository.findById(userId);
        if(tempUser.isEmpty()){
            return false;
        }
        if(userMateRepository.findById(mateId).isEmpty()){
            return false;
        }
        UserMate temp = new UserMate(null, userId, tempUser.get());
        userMateRepository.save(temp);
        return true;
    }

//    public List<UserMate> getMateList(Long userId){
//        return userMateRepository.findAllByMateid(userId);
//    }
}
