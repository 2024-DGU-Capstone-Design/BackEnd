package com.example.budd_server.service;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.CallRecordRepository;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RiskLevelService {
    @Autowired
    private UserRepository userRepository;  // User 저장소

    @Autowired
    private CallRecordRepository callRecordRepository;  // CallRecord 저장소

    // 모든 사용자에 대해 위험군 재분류 작업
    @Scheduled(cron = "0 0 0 31 3,6,9,12 ?")
    public void reclassifyAllUsersRiskLevels() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<CallRecord> callRecords = callRecordRepository.findByUserId(user.getUserId());
            reclassifyRiskLevel(user, callRecords);
        }
    }

    // 위험군 재분류
    public void reclassifyRiskLevel(User user, List<CallRecord> callRecords) {
        LocalDate now = LocalDate.now();
        LocalDate startOfQuarter = getStartOfQuarter(now);

        int totalPoints = calculateTotalPoints(callRecords, startOfQuarter);
        int userPoints = calculateUserPoints(callRecords, startOfQuarter);

        double scoreRatio = (double) userPoints / totalPoints;

        updateRiskLevel(user, scoreRatio);
    }

    private LocalDate getStartOfQuarter(LocalDate date) {
        int month = (date.getMonthValue() - 1) / 3 * 3 + 1;
        return LocalDate.of(date.getYear(), month, 1);
    }

    // 총점 계산
    private int calculateTotalPoints(List<CallRecord> records, LocalDate startOfQuarter) {
        int totalPoints = 0;

        for (CallRecord record : records) {
            LocalDate date = record.getScheduledDate();
            if (date.isAfter(startOfQuarter.plusMonths(2).minusDays(1))) {
                totalPoints += 3;
            } else if (date.isAfter(startOfQuarter.plusMonths(1).minusDays(1))) {
                totalPoints += 2;
            } else {
                totalPoints += 1;
            }
        }

        return totalPoints;
    }


    // user 점수 계산
    private int calculateUserPoints(List<CallRecord> records, LocalDate startOfQuarter) {
        int userPoints = 0;

        for (CallRecord record : records) {
            if (record.getStatus() == CallRecord.CallStatus.completed) {
                LocalDate date = record.getScheduledDate();
                if (date.isAfter(startOfQuarter.plusMonths(2).minusDays(1))) {
                    userPoints += 3;
                } else if (date.isAfter(startOfQuarter.plusMonths(1).minusDays(1))) {
                    userPoints += 2;
                } else {
                    userPoints += 1;
                }
            }
        }
        return userPoints;
    }

    // 위험군 단계 수정
    private void updateRiskLevel(User user, double scoreRatio) {
        String riskLevel = user.getRiskLevel();
        if (scoreRatio < 0.6 && riskLevel.equals("저")) {
            user.setRiskLevel("중");
        } else if (scoreRatio < 0.6 && riskLevel.equals("중")) {
            user.setRiskLevel("고");
        } else if (scoreRatio >= 0.9 && riskLevel.equals("중")) {
            user.setRiskLevel("저");
        } else if (scoreRatio >= 0.9 && riskLevel.equals("고")) {
            user.setRiskLevel("중");
        }

        // 변경된 사용자 정보를 데이터베이스에 저장
        userRepository.save(user);
    }
}
