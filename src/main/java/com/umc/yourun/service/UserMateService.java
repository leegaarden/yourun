package com.umc.yourun.service;
import com.umc.yourun.repository.UserMateRepository;
import com.umc.yourun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMateService {
    UserMateRepository userMateRepository;
    UserRepository userRepository;

    @Autowired
    public UserMateService(UserMateRepository userMateRepository, UserRepository userRepository) {
        this.userMateRepository = userMateRepository;
        this.userRepository = userRepository;
    }

//    public Boolean addmate(Long userId,Long mateId){
//        Optional<User> tempUser = userRepository.findById(userId);
//        if(tempUser.isEmpty()){
//            return false;
//        }
//        if(userMateRepository.findById(mateId).isEmpty()){
//            return false;
//        }
//        userMateRepository.save(UserMate.builder().id(null).user(tempUser.get()).mateid(userId).build());
//        return true;
//    }

//    public List<UserMate> getMateList(Long userId){
//        return userMateRepository.findAllByMateid(userId);
//    }
}
