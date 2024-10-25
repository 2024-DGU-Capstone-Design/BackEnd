package com.example.budd_server.controller;

import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ReportRepository;
import com.example.budd_server.repository.UserRepository;
import com.example.budd_server.service.DailyCheckService;
import com.example.budd_server.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
public class TestSmsController {
    @Autowired
    private SmsService smsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private DailyCheckService dailyCheckService;

    @GetMapping("/test/sendReport/{userId}")
    public String sendTestReport(@PathVariable int userId) {
        try {
            // Fetch user details by userId using Optional
            Optional<User> optionalUser = userRepository.findByUserId(userId);

            if (optionalUser.isEmpty()) {
                return "유저를 찾을 수 없습니다.";
            }

            User user = optionalUser.get();

            LocalDate startOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate startOfNextMonth = startOfPreviousMonth.plusMonths(1);

            Report report = reportRepository.findByUserIdAndMonth(userId, startOfPreviousMonth, startOfNextMonth);
            if (report == null) {
                return "이전 달의 리포트 데이터가 없습니다.";
            }

            String monthString = String.format("%02d월", startOfPreviousMonth.getMonthValue());
            String monthForUrl = String.format("%02d", startOfPreviousMonth.getMonthValue());

            String message = String.format("%s님의 %s 종합 리포트를 확인해 보세요! https://budd-report.vercel.app/report/%d/%s",
                    user.getName(), monthString, userId, monthForUrl);

            smsService.sendSms(user.getContact1(), message);

            return "사용자의 리포트를 성공적으로 전송했습니다.";
        } catch (Exception e) {
            return "Error 발생: " + e.getMessage();
        }
    }

    @GetMapping("/test/dailyCheck")
    public String runDailyCheck() {
        dailyCheckService.checkUserResponses();
        return "Daily check 실행 완료";
    }
}
