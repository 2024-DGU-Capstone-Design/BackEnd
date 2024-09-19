package com.example.budd_server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 어노테이션 추가
@EnableMongoRepositories(basePackages = "com.example.budd_server.repository")
public class BuddServerApplication {

	public static void main(String[] args) {
		// .env 파일에서 환경 변수 로드
		Dotenv dotenv = Dotenv.load();

		// 환경 변수에서 MongoDB URI 가져오기
		String mongodbUri = dotenv.get("MONGODB_URI");
		System.out.println("MongoDB URI: " + mongodbUri); // 확인용 출력 (실제 배포 시엔 제거)

		// Spring Boot에 MongoDB URI 설정
		if (mongodbUri != null) {
			System.setProperty("spring.data.mongodb.uri", mongodbUri);
		}

		SpringApplication.run(BuddServerApplication.class, args);
	}
}