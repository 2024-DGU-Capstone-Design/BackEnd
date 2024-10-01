package com.example.budd_server.service;

import com.example.budd_server.entity.User;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // spring이 인스턴스 자동 주입

    @Autowired
    private ScheduleCallService scheduleCallService;

    // 전체 사용자 목록을 페이지네이션을 적용하여 가져오는 메소드
    public Page<User> getAllUsersByPage(int page) {
        Pageable pageable = PageRequest.of(page, 10);  // 페이지 번호와 페이지 크기(10명) 설정
        Page<User> usersPage = userRepository.findAll(pageable);

        // 현재 페이지가 전체 페이지 수를 초과하는 경우
        if (page >= usersPage.getTotalPages()) {
            throw new IllegalArgumentException("더 이상 페이지가 존재하지 않습니다.");
        }

        return usersPage;
    }


    public List<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    public void deleteUserByUserId(int userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            String id = userOptional.get().getId();
            userRepository.deleteById(id); // ObjectId로 삭제
        } else {
            throw new NoSuchElementException("User not found with userId: " + userId);
        }
    }

    public User updateUser(User existingUser) {
        return userRepository.save(existingUser);
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findByUserId(userId);
    }

    public User createUser(User newUser) {
        // 현재 최대 userId를 조회
        Integer maxUserId = userRepository.findMaxUserId();

        // 새로운 userId 설정
        if (maxUserId == null) {
            newUser.setUserId(1); // 첫 번째 사용자일 경우
        } else {
            newUser.setUserId(maxUserId + 1); // 현재 최대 userId에 1을 더함
        }

        // 나이에 따라 위험군 설정
        if (newUser.getAge() >= 80) {
            newUser.setRiskLevel("고");
        } else if (newUser.getAge() < 70) {
            newUser.setRiskLevel("저");
        } else {
            newUser.setRiskLevel("중");
        }

        // 새로운 사용자 생성
        User createdUser = userRepository.save(newUser);

        // 새로운 사용자 추가 시, 전화 일정 생성
        scheduleCallService.scheduleCallsForNewUser(createdUser);

        return createdUser;
    }
}


