package com.example.budd_server.controller;

import com.example.budd_server.entity.CallRecord;
import com.example.budd_server.entity.User;
import com.example.budd_server.service.ScheduleCallService;
import com.example.budd_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleCallService scheduleCallService;

    // 새로운 사용자를 추가하고 통화 일정을 테스트
    @PostMapping("/addUserWithCalls")
    public ResponseEntity<?> addUserAndScheduleCalls(@RequestBody User newUser) {
        // 새로운 사용자 추가
        User createdUser = userService.createUser(newUser);

        // 새로운 사용자에 대한 통화 일정 생성 (다음 분기 전까지)
        scheduleCallService.scheduleCallsForNewUser(createdUser);

        // 생성된 통화 일정을 조회
        List<CallRecord> callRecords = scheduleCallService.getCallRecordsForUser(createdUser.getUserId());

        // 결과 반환
        return ResponseEntity.ok(callRecords);
    }
}
