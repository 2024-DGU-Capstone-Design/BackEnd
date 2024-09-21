package com.example.budd_server.service;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.CallRecordRepository;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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

    /// 새로운 사용자가 추가될 때 일정을 생성하는 메소드 (추가부분)
    public void scheduleCallsForNewUser(User user) {
        LocalDate today = LocalDate.now();
        LocalDate firstCallDate = today.plusDays(1); // 추가된 날로부터 하루 뒤

        int intervalDays = getCallIntervalDays(user.getRiskLevel());

        // 이번 분기의 종료일을 계산
        LocalDate startOfQuarter = getStartOfQuarter(firstCallDate);
        LocalDate endOfQuarter = startOfQuarter.plusMonths(3).minusDays(1);

        // 하루 차이 보정 (MongoDB 하루 차이 방지)
        LocalDate adjustedFirstCallDate = firstCallDate.plusDays(1);
        LocalDate adjustedEndOfQuarter = endOfQuarter.plusDays(1);

        // 이미 생성된 일정이 있는지 확인
        List<CallRecord> existingRecords = callRecordRepository.findByUserId(user.getUserId());
        if (!existingRecords.isEmpty()) {
            System.out.println("이미 생성된 통화 일정이 있음: " + existingRecords);
            return;  // 이미 일정이 있으면 새로 생성하지 않음
        }

        // 저장활 리스트 생성
        List<CallRecord> callSchedule = new ArrayList<>();
        LocalDate scheduledDate = adjustedFirstCallDate;

        // 종료 날짜를 포함하여 일정 생성
        while (!scheduledDate.isAfter(adjustedEndOfQuarter)) {
            CallRecord callRecord = new CallRecord();
            callRecord.setUserId(user.getUserId());
            callRecord.setScheduledDate(scheduledDate);  // 날짜 저장

            callRecord.setStatus(CallRecord.CallStatus.scheduled);
            callSchedule.add(callRecord);

            // 다음 일정 날짜 설정
            scheduledDate = scheduledDate.plusDays(intervalDays);

            System.out.println("현재 생성하려는 통화 일정 날짜: " + scheduledDate);
        }

        // 전화 기록을 DB에 저장
        callRecordRepository.saveAll(callSchedule);

        System.out.println("새로운 사용자에 대한 전화 일정 생성됨: " + callSchedule);
    }




    // 위험도에 따른 전화 주기 설정 (기존)
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

    // 주기적으로 전화 일정 생성 (기존)
    private List<CallRecord> createCallSchedule(int userId, LocalDate startDate, LocalDate endDate, int intervalDays) {
        List<CallRecord> callSchedule = new ArrayList<>();
        LocalDate scheduledDate = startDate;

        // 주기적으로 전화 일정 생성
        while (!scheduledDate.isAfter(endDate)) {
            System.out.println("현재 생성하려는 통화 일정 날짜: " + scheduledDate);

            // 중복된 날짜가 있는지 확인
            if (!callSchedule.isEmpty() && callSchedule.get(callSchedule.size() - 1).getScheduledDate().equals(scheduledDate)) {
                System.out.println("중복된 날짜 발견: " + scheduledDate + ". 루프 종료.");
                break;  //  같은 날짜가 있으면 종료
            }

            CallRecord callRecord = new CallRecord();
            callRecord.setUserId(userId);
            callRecord.setScheduledDate(scheduledDate);
            callRecord.setStatus(CallRecord.CallStatus.scheduled);

            callSchedule.add(callRecord);

            // 다음 전화 일정을 주기적으로 설정
            scheduledDate = scheduledDate.plusDays(intervalDays);

            System.out.println("다음 통화 일정 날짜: " + scheduledDate);
        }

        System.out.println("총 생성된 통화 일정: " + callSchedule.size() + "건");
        return callSchedule;
    }



    // 분기 시작일 계산 (기존)
    private LocalDate getStartOfQuarter(LocalDate date) {
        int month = (date.getMonthValue() - 1) / 3 * 3 + 1;
        return LocalDate.of(date.getYear(), month, 1);
    }

    public List<CallRecord> getCallRecordsForUser(int userId) {
        return callRecordRepository.findByUserId(userId);
    }

}
