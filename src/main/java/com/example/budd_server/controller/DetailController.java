package com.example.budd_server.controller;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.Response;
import com.example.budd_server.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 요청할 때(기존)
/* 사용자의 응답 데이터를 조회하려면: /api/call/1/response
   사용자의 레포트 데이터를 조회하려면: /api/call/1/report
   사용자의 통화 기록을 조회하려면: /api/call/1/callRecord */

// 요청할 때( 수정 후 )
        /* 응답 데이터 조회: /api/detail/{userId}/response
        리포트 데이터 조회: /api/detail/{userId}/report
        통화 기록 조회: /api/detail/{userId}/callRecord */

@RestController
@RequestMapping("/api")
public class DetailController {
    @Autowired
    private DetailService detailService;

    @GetMapping("/detail/{userId}/{type}")
    public ResponseEntity<?> getData(@PathVariable int userId, @PathVariable String type) {
        if ("response".equalsIgnoreCase(type)) {
            // 응답 데이터 조회
            List<Response> responses = detailService.getResponse(userId);
            if (responses == null || responses.isEmpty()) {
                return ResponseEntity.status(404).body("Response 데이터를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(responses);
        } else if ("report".equalsIgnoreCase(type)) {
            // 리포트 데이터 조회
            List<Report> reports = detailService.getReportByUserIdAndMonth(userId);
            if (reports == null || reports.isEmpty()) {
                return ResponseEntity.status(404).body("Report 데이터를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(reports);
        } else if ("callRecord".equalsIgnoreCase(type)) {
            // 통화 기록 데이터 조회
            List<CallRecord> callRecords = detailService.getCallRecordByUserIdAndYear(userId);
            if (callRecords == null || callRecords.isEmpty()) {
                return ResponseEntity.status(404).body("callRecords 데이터를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(callRecords);
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 파라미터 입니다.");
        }
    }
}
