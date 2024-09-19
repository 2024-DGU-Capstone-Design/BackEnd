package com.example.budd_server.service;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.CallRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

@Service
public class ScheduleAdditionalCallService {

    @Autowired
    private CallRecordRepository callRecordRepository;

    public void scheduleAdditionalCall(User user, LocalDate originalCallDate) {
        LocalDate additionalCallDate;

        // 위험군에 따른 추가 통화 일정 계산
        switch (user.getRiskLevel()) {
            case "저":
                additionalCallDate = originalCallDate.plusDays(4);
                break;
            case "중":
                additionalCallDate = originalCallDate.plusDays(2);
                break;
            case "고":
                additionalCallDate = originalCallDate.plusDays(1);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 위험군: " + user.getRiskLevel());
        }

        // 새로운 통화 기록 생성
        CallRecord additionalCallRecord = new CallRecord();
        additionalCallRecord.setUserId(user.getUserId());
        additionalCallRecord.setScheduledDate(additionalCallDate);
        additionalCallRecord.setStatus(CallRecord.CallStatus.scheduled);

        // DB에 저장
        callRecordRepository.save(additionalCallRecord);

        System.out.println("추가 통화 기록 생성됨: " + additionalCallRecord);
    }
}
