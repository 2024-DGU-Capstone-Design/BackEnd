package com.example.budd_server.controller;

import com.example.budd_server.entity.User;
import com.example.budd_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 모든 사용자 조회
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        System.out.println("Users from service: " + users); // 로그 추가
        return ResponseEntity.ok(users);
    }

    // 이름으로 사용자 조회
    @GetMapping("/users/{name}")
    public ResponseEntity<List<User>> searchUser(@PathVariable String name) {
        List<User> users = userService.getUserByName(name);
        System.out.println("Search Users from service: " + users);
        return ResponseEntity.ok(users);
    }

    // 사용자 추가
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User createdUser = userService.createUser(newUser);
        return ResponseEntity.ok(createdUser);
    }
}
