package com.example.budd_server.controller;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.Response;
import com.example.budd_server.service.ModalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 요청할 때
/* 사용자의 응답 데이터를 조회하려면: /api/call/1/response
   사용자의 레포트 데이터를 조회하려면: /api/call/1/report
   사용자의 통화 기록을 조회하려면: /api/call/1/callRecord */

@RestController
@RequestMapping("/api")
public class ModalController {
    @Autowired
    private ModalService modalService;

    @GetMapping("/call/{userId}/{type}")
    public ResponseEntity<?> getData(@PathVariable int userId, @PathVariable String type) {
        if ("response".equalsIgnoreCase(type)) {
            List<Response> responses = modalService.getResponse(userId);
            if (responses == null || responses.isEmpty()) {
                return ResponseEntity.status(404).body("Response 데이터를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(responses);
        } else if ("report".equalsIgnoreCase(type)) {
            List<Report> reports = modalService.getReportByUserIdAndMonth(userId);
            if (reports == null || reports.isEmpty()) {
                return ResponseEntity.status(404).body("Report 데이터를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(reports);
        } else if ("callRecord".equalsIgnoreCase(type)) {
            List<CallRecord> callRecords = modalService.getCallRecordByUserIdAndMonth(userId);
            if (callRecords == null || callRecords.isEmpty()) {
                return ResponseEntity.status(404).body("callRecords 데이터를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(callRecords);
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 파라미터 입니다.");
        }
    }
}
