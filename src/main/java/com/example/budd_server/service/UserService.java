package com.example.budd_server.service;

import com.example.budd_server.entity.User;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // spring이 인스턴스 자동 주입

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("Retrieved users from repository: " + users); // 로그 추가
        return users;
    }

    public List<User> getUserByName(String name){
        return userRepository.findByName(name);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    } // 사용자 추가 또는 수정

}
