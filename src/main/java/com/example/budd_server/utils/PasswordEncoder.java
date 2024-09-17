package com.example.budd_server.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github.cdimascio.dotenv.Dotenv;

public class PasswordEncoder {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String email = dotenv.get("EMAIL");
        String rawPassword = dotenv.get("PASSWORD");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("암호화된 비밀번호: " + encodedPassword);
    }
}

