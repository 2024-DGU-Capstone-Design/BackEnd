package com.example.budd_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**") // URL 경로
                .addResourceLocations("file:external/") // 실제 파일 저장 경로
                .setCachePeriod(0); // 캐싱 비활성화
    }
}
