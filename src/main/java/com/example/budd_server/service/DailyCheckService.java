package com.example.budd_server.service;

import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DailyCheckService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    @Scheduled(cron = "0 0 18 * * ?") // 매일 저녁 6시에 실행
    public void checkUserResponses() {
        LocalDate today = LocalDate.now();
        System.out.println("Today's date: " + today); // 현재 날짜 출력

        LocalDateTime todayStart = LocalDate.now().atStartOfDay(); // 오늘 시작 시각
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59); // 오늘 종료 시각
        List<Response> responses = responseRepository.findByDateBetween(todayStart, todayEnd);
        System.out.println("Number of responses: " + responses.size()); // 응답 개수 출력

        for (Response response : responses) {
            Optional<User> optionalUser = userRepository.findByUserId(response.getUserId());
            System.out.println("Processing response for userId: " + response.getUserId()); // userId 출력

            // 사용자 정보가 존재하는지 확인
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                System.out.println("User found: " + user.getName());

                // 질병이 있고 약을 안 먹었을 경우
                if (response.getDisease() && !response.getMedicine()) {
                    String message = String.format("%s 님께서는 질병이 있으나 약을 복용하지 않으셨습니다. 병원 방문을 권유 드립니다.", user.getName());
                    smsService.sendSms(user.getContact1(), message);
                    // 추후 보호자 2에게도 전송해야 함
                }
                // 식사와 기분이 모두 부정적일 경우
                else if (!response.getMeal() && !response.getMood()) {
                    String message = String.format("%s 님께서 오늘 식사 및 기분 상태가 좋지 않으셨습니다. 확인 연락을 권유 드립니다.", user.getName());
                    smsService.sendSms(user.getContact1(), message);
                }
            } else {
                System.out.println("User not found for userId: " + response.getUserId());
            }
        }
    }
}
