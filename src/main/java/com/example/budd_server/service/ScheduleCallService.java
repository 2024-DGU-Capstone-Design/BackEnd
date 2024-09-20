package com.example.budd_server.service;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.CallRecordRepository;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleCallService {

    @Autowired
    CallRecordRepository callRecordRepository;

    @Autowired
    UserRepository userRepository;

    // 이번 분기의 전화 일정 생성 (모든 사용자 대상)
    @Scheduled(cron = "0 0 1 1 1,4,7,10 ?") // 분기 첫 날 새벽 1시에 자동 실행
    public void scheduleCurrentQuarterCallsForAllUsers() {
        // 모든 사용자를 가져옴
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String riskLevel = user.getRiskLevel();
            int userId = user.getUserId();

            // 이번 분기의 시작일과 종료일을 계산
            LocalDate now = LocalDate.now();
            LocalDate startOfQuarter = getStartOfQuarter(now).plusDays(1);
            LocalDate endOfQuarter = startOfQuarter.plusMonths(3).minusDays(1);  // 이번 분기 마지막 날

            // 전화 일정 생성
            List<CallRecord> callSchedule = createCallSchedule(userId, startOfQuarter, endOfQuarter, getCallIntervalDays(riskLevel));

            // DB에 저장
            callRecordRepository.saveAll(callSchedule);

            System.out.println("Scheduled current quarter calls for userId " + userId + ": " + callSchedule);
        }
    }

    // 분기의 시작일 계산
    private LocalDate getStartOfQuarter(LocalDate date) {
        int month = (date.getMonthValue() - 1) / 3 * 3 + 1;
        return LocalDate.of(date.getYear(), month, 1);
    }

    // 위험도에 따른 전화 주기 설정
    private int getCallIntervalDays(String riskLevel) {
        switch (riskLevel) {
            case "고":
                return 2;  // 2일마다 전화
            case "중":
                return 4;  // 4일마다 전화
            case "저":
                return 7;  // 7일마다 전화
            default:
                throw new IllegalArgumentException("Unknown risk level: " + riskLevel);
        }
    }

    // 주기 전화 일정 생성
    private List<CallRecord> createCallSchedule(int userId, LocalDate startDate, LocalDate endDate, int intervalDays) {
        List<CallRecord> callSchedule = new ArrayList<>();
        LocalDate scheduledDate = startDate;

        // 주기적으로 전화 일정 생성
        while (!scheduledDate.isAfter(endDate)) {
            CallRecord callRecord = new CallRecord();
            callRecord.setUserId(userId);
            callRecord.setScheduledDate(scheduledDate);
            callRecord.setStatus(CallRecord.CallStatus.scheduled);

            callSchedule.add(callRecord);

            // 다음 주기 전화 날짜로 설정
            scheduledDate = scheduledDate.plusDays(intervalDays);
        }

        return callSchedule;
    }
}