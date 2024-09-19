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
public class DetailService {
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

    // 1년 단위로 통화 기록 조회
    public List<CallRecord> getCallRecordByUserIdAndYear(int userId) {
        LocalDate now = LocalDate.now();

        // 현재 년도의 시작일과 종료일 계산
        LocalDate startYear = now.withMonth(1).withDayOfMonth(1);  // 해당 년도의 1월 1일
        LocalDate endYear = now.withMonth(12).withDayOfMonth(31);  // 해당 년도의 12월 31일

        List<CallRecord> callRecords = callRecordRepository.findByUserIdAndScheduledDateBetween(userId, startYear, endYear);
        System.out.println("Retrieved callRecords from repository (for the year): " + callRecords);
        return callRecords;
    }
}


