package com.example.budd_server.service;

import com.example.budd_server.dto.UserDetailDto;
import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.CallRecordRepository;
import com.example.budd_server.repository.ReportRepository;
import com.example.budd_server.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DetailService {
    @Autowired
    UserService userService;

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    CallRecordRepository callRecordRepository;

    public UserDetailDto getUserDetails(int userId) {
        // 사용자 정보 조회
        User user = userService.getUserById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate previousMonth = now.minusMonths(1).withDayOfMonth(1); // 이전 달 시작일
        LocalDate endPreviousMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth()); // 이전 달 마지막일

        // 응답 데이터 조회
        List<Response> responses = responseRepository.findByUserIdAndDateBetween(userId, firstDayOfMonth, lastDayOfMonth);

        // 리포트 데이터 조회
        List<Report> reports = reportRepository.findByUserIdAndMonthBetween(userId, previousMonth, endPreviousMonth);

        // 통화 기록 조회 (현재 년도 기준)
        LocalDate startYear = now.withMonth(1).withDayOfMonth(1);
        LocalDate endYear = now.withMonth(12).withDayOfMonth(31);
        List<CallRecord> callRecords = callRecordRepository.findByUserIdAndScheduledDateBetween(userId, startYear, endYear);

        // 모든 데이터를 DTO로 묶어서 반환
        return new UserDetailDto(user, callRecords, responses, reports);
    }

    // 이번 달의 세부 응답 조회
    public List<Response> getResponse(int userId) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Response> responses = responseRepository.findByUserIdAndDateBetween(userId, firstDayOfMonth, lastDayOfMonth);

        System.out.println("Retrieved responses from repository: " + responses);
        return responses;
    }

    // 특정 달의 리포트 조회
    public List<Report> getReportByUserIdAndMonth(int userId, int month) {
        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.withMonth(month).withDayOfMonth(1); // 지정된 달의 시작일
        LocalDate endMonth = startMonth.withDayOfMonth(startMonth.lengthOfMonth()); // 지정된 달의 마지막일

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

