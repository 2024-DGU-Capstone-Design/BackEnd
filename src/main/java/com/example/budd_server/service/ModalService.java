package com.example.budd_server.service;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.Response;
import com.example.budd_server.repository.CallRecordRepository;
import com.example.budd_server.repository.ReportRepository;
import com.example.budd_server.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ModalService {
    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    CallRecordRepository callRecordRepository;

    // 이번 달의 세부 응답 조회
    public List<Response> getResponse(int userId) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Response> responses = responseRepository.findByUserIdAndDateBetween(userId, firstDayOfMonth, lastDayOfMonth);
        System.out.println("Retrieved responses from repository: " + responses);
        return responses;
    }

    // 이번 달의 리포트 조회
    public List<Report> getReportByUserIdAndMonth(int userId) {
        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.withDayOfMonth(1); // 이번 달 시작일
        LocalDate endMonth = now.withDayOfMonth(now.lengthOfMonth()); // 이번 달 마지막일

        List<Report> reports = reportRepository.findByUserIdAndMonthBetween(userId, startMonth, endMonth);
        System.out.println("Retrieved reports from repository: " + reports);
        return reports;
    }

    // 이번 분기의 통화 기록 조회
    public List<CallRecord> getCallRecordByUserIdAndMonth(int userId) {
        LocalDate now = LocalDate.now();

        // 현재 달을 기준으로 분기의 시작 월과 종료 월 계산
        int currentMonth = now.getMonthValue();
        int startQuarterMonth = ((currentMonth - 1) / 3) * 3 + 1;  // 1, 4, 7, 10 중 하나
        LocalDate startQuarter = now.withMonth(startQuarterMonth).withDayOfMonth(1);  // 분기 시작일
        LocalDate endQuarter = startQuarter.plusMonths(2).withDayOfMonth(startQuarter.plusMonths(2).lengthOfMonth());  // 분기 종료일

        List<CallRecord> callRecords = callRecordRepository.findByUserIdAndScheduledDateBetween(userId, startQuarter, endQuarter);
        System.out.println("Retrieved callRecords from repository: " + callRecords);
        return callRecords;
    }
}


