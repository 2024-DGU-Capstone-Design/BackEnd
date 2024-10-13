package com.example.budd_server.controller;

import com.example.budd_server.dto.ChatGPTDto;
import com.example.budd_server.service.ChatGPTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/chatGpt")
@CrossOrigin(origins = {"https://budd-client.vercel.app", "http://localhost:5173", "https://budd-report.vercel.app/"})
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/legacyPrompt")
    public ResponseEntity<Map<String, Object>> selectLegacyPrompt(@RequestBody Map<String, String> requestData) {
        ChatGPTDto chatGPTDto = new ChatGPTDto();

        // 입력 데이터를 받아서 프롬프트 설정
        chatGPTDto.setPrompt(
                requestData.get("name"),
                requestData.get("mealStatus"),
                requestData.get("diseaseStatus"),
                requestData.get("medicationStatus"),
                requestData.get("emotionalStatus")
        );

        Map<String, Object> result = chatGPTService.legacyPrompt(chatGPTDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 유저 아이디 받아서 자동 프롬프트 설정 후 ChatGPT API 호출
    @PostMapping("/generateReport/{userId}")
    public ResponseEntity<Map<String, Object>> generateUserReport(@PathVariable int userId) {
        // 서비스에서 리포트를 생성
        chatGPTService.generateUserReport(userId);

        // 성공 메시지를 Map으로 만들어 반환
        Map<String, Object> response = Map.of("message", "리포트가 성공적으로 생성되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 모든 유저 대상으로 report 추가해주는 기능 (테스트용)
    @PostMapping("/generateReport/ALL")
    public ResponseEntity<String> generateReports() {
        chatGPTService.generateReportsForAllUsers();
        return new ResponseEntity<>("모든 사용자에 대한 리포트 생성 완료", HttpStatus.OK);
    }
}
