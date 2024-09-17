package com.example.budd_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // CORS 설정을 적용할 경로
                .allowedOrigins("http://localhost:8080") // 허용할 출처
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true); // 쿠키를 사용할 경우 true로 설정
    }
}
