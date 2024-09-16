package com.example.budd_server.controller;


import com.example.budd_server.dto.LoginDto;
import com.example.budd_server.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private AdminService adminService;

    //로그인 api
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto logindto, HttpServletRequest request) {
        System.out.println("로그인 시도: " + logindto.getEmail());  // 이 부분이 출력되는지 확인
        try {
            adminService.login(logindto.getEmail(), logindto.getPassword(), request);
            System.out.println("로그인 성공 메시지 전송 중");  // 이 로그가 출력되는지 확인
            return ResponseEntity.ok("로그인 성공");  // 이 부분이 반환되는지 확인
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        adminService.logout(request);
        return ResponseEntity.ok("로그아웃 성공");
    }
}