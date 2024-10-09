package com.example.budd_server.controller;

import com.example.budd_server.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioController {

    @Autowired
    private QuestionService questionService;  // 질문 서비스

    @PostMapping("/twilio/handle-recording")
    public String handleRecording(@RequestParam(value = "SpeechResult", required = false) String speechResult) {
        try {
            // SpeechResult 확인 로그
            System.out.println("Received SpeechResult: " + speechResult);

            // SpeechResult가 비어있지 않다면 음성 인식 결과를 처리
            if (speechResult != null && !speechResult.isEmpty()) {
                return questionService.handleResponse(speechResult);  // 질문 서비스에서 응답 처리
            } else {
                return "응답이 없습니다. 질문을 반복합니다.";
            }
        } catch (Exception e) {
            System.err.println("Error processing recording: " + e.getMessage());
            return "처리 중 오류가 발생했습니다.";
        }
    }

}
