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

		// 환경 변수에서 MongoDB URI와 Twilio 정보 가져오기
		String mongodbUri = dotenv.get("MONGODB_URI");
		String twilioAccountSid = dotenv.get("TWILIO_ACCOUNT_SID");
		String twilioAuthToken = dotenv.get("TWILIO_AUTH_TOKEN");

		// 확인용 출력 (실제 배포 시엔 제거)
		System.out.println("MongoDB URI: " + mongodbUri);
		System.out.println("Twilio Account SID: " + twilioAccountSid);

		// Spring Boot에 MongoDB URI 설정
		if (mongodbUri != null) {
			System.setProperty("spring.data.mongodb.uri", mongodbUri);
		}

		// Twilio 환경 변수 설정
		if (twilioAccountSid != null && twilioAuthToken != null) {
			System.setProperty("TWILIO_ACCOUNT_SID", twilioAccountSid);
			System.setProperty("TWILIO_AUTH_TOKEN", twilioAuthToken);
		}

		SpringApplication.run(BuddServerApplication.class, args);
	}
}
