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

        // 유저의 리포트를 생성하고 ChatGPT API로 전송할 데이터를 가공
        ChatGPTDto chatGPTDto = chatGPTService.generateUserReport(userId);

        // chatGPTDto가 null인 경우, 이미 리포트가 존재함을 알리는 응답 반환
        if (chatGPTDto == null) {
            return new ResponseEntity<>(Map.of("message", "이미 리포트가 존재합니다."), HttpStatus.CONFLICT);
        }

        // ChatGPT API 호출
        Map<String, Object> result = chatGPTService.legacyPrompt(chatGPTDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
