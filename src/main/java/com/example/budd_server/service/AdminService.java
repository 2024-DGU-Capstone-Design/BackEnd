package com.example.budd_server.service;

import com.example.budd_server.entity.Admin;
import com.example.budd_server.repository.AdminRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /// 로그인 처리
    public boolean login(String email, String password, HttpServletRequest request) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 다시 입력하세요."));
        System.out.println("사용자가 입력한 비밀번호: " + password);
        System.out.println("DB에 저장된 암호화된 비밀번호: " + admin.getPassword());

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 다시 입력하세요.");
        }

        // 세션에 관리자 정보 저장
        request.getSession().setAttribute("ADMIN_SESSION", admin);
        System.out.println("세션 ID: " + request.getSession().getId());
        return true;
    }

    // 로그아웃 처리
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();  // 세션 무효화
    }
}
